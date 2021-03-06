/**
 * Copyright 2019 The JoyQueue Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chubao.joyqueue.broker.protocol.handler;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import io.chubao.joyqueue.broker.BrokerContext;
import io.chubao.joyqueue.broker.BrokerContextAware;
import io.chubao.joyqueue.broker.protocol.JoyQueueCommandHandler;
import io.chubao.joyqueue.broker.buffer.Serializer;
import io.chubao.joyqueue.broker.consumer.Consume;
import io.chubao.joyqueue.broker.consumer.model.PullResult;
import io.chubao.joyqueue.broker.helper.SessionHelper;
import io.chubao.joyqueue.domain.Partition;
import io.chubao.joyqueue.exception.JoyQueueCode;
import io.chubao.joyqueue.exception.JoyQueueException;
import io.chubao.joyqueue.message.BrokerMessage;
import io.chubao.joyqueue.message.MessageLocation;
import io.chubao.joyqueue.network.command.BooleanAck;
import io.chubao.joyqueue.network.command.CommitAckData;
import io.chubao.joyqueue.network.command.CommitAckRequest;
import io.chubao.joyqueue.network.command.CommitAckResponse;
import io.chubao.joyqueue.network.command.JoyQueueCommandType;
import io.chubao.joyqueue.network.command.RetryType;
import io.chubao.joyqueue.network.session.Connection;
import io.chubao.joyqueue.network.session.Consumer;
import io.chubao.joyqueue.network.transport.Transport;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.Type;
import io.chubao.joyqueue.server.retry.api.MessageRetry;
import io.chubao.joyqueue.server.retry.model.RetryMessageModel;
import io.chubao.joyqueue.toolkit.lang.ListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * CommitAckRequestHandler
 *
 * author: gaohaoxiang
 * date: 2018/12/12
 */
public class CommitAckRequestHandler implements JoyQueueCommandHandler, Type, BrokerContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(CommitAckRequestHandler.class);

    private Consume consume;
    private MessageRetry retryManager;

    @Override
    public void setBrokerContext(BrokerContext brokerContext) {
        this.consume = brokerContext.getConsume();
        this.retryManager = brokerContext.getRetryManager();
    }

    @Override
    public Command handle(Transport transport, Command command) {
        CommitAckRequest commitAckRequest = (CommitAckRequest) command.getPayload();
        Connection connection = SessionHelper.getConnection(transport);

        if (connection == null || !connection.isAuthorized(commitAckRequest.getApp())) {
            logger.warn("connection is not exists, transport: {}, app: {}", transport, commitAckRequest.getApp());
            return BooleanAck.build(JoyQueueCode.FW_CONNECTION_NOT_EXISTS.getCode());
        }

        Table<String, Short, JoyQueueCode> result = HashBasedTable.create();

        for (Map.Entry<String, Map<Short, List<CommitAckData>>> entry : commitAckRequest.getData().rowMap().entrySet()) {
            String topic = entry.getKey();
            for (Map.Entry<Short, List<CommitAckData>> partitionEntry : entry.getValue().entrySet()) {
                JoyQueueCode ackCode = commitAck(connection, topic, commitAckRequest.getApp(), partitionEntry.getKey(), partitionEntry.getValue());
                result.put(topic, partitionEntry.getKey(), ackCode);
            }
        }

        CommitAckResponse commitAckResponse = new CommitAckResponse();
        commitAckResponse.setResult(result);
        return new Command(commitAckResponse);
    }

    protected JoyQueueCode commitAck(Connection connection, String topic, String app, short partition, List<CommitAckData> dataList) {
        if (partition == Partition.RETRY_PARTITION_ID) {
            return doCommitRetry(connection, topic, app, partition, dataList);
        } else {
            return doCommitAck(connection, topic, app, partition, dataList);
        }
    }

    protected JoyQueueCode doCommitRetry(Connection connection, String topic, String app, short partition, List<CommitAckData> dataList) {
        try {
            List<Long> retrySuccess = Lists.newLinkedList();
            List<Long> retryError = Lists.newLinkedList();

            for (CommitAckData commitAckData : dataList) {
                if (commitAckData.getRetryType().equals(RetryType.NONE)) {
                    retrySuccess.add(commitAckData.getIndex());
                } else {
                    retryError.add(commitAckData.getIndex());
                }
            }

            if (CollectionUtils.isNotEmpty(retrySuccess)) {
                retryManager.retrySuccess(topic, app, ListUtil.toArray(retrySuccess));
            }

            if (CollectionUtils.isNotEmpty(retryError)) {
                retryManager.retryError(topic, app, ListUtil.toArray(retryError));
            }

            return JoyQueueCode.SUCCESS;
        } catch (JoyQueueException e) {
            logger.error("commit ack exception, topic: {}, app: {}, partition: {}, transport: {}", topic, app, partition, connection.getTransport(), e);
            return JoyQueueCode.valueOf(e.getCode());
        } catch (Exception e) {
            logger.error("commit ack exception, topic: {}, app: {}, partition: {}, transport: {}", topic, app, partition, connection.getTransport(), e);
            return JoyQueueCode.CN_UNKNOWN_ERROR;
        }
    }

    protected JoyQueueCode doCommitAck(Connection connection, String topic, String app, short partition, List<CommitAckData> dataList) {
        try {
            MessageLocation[] messageLocations = new MessageLocation[dataList.size()];
            List<CommitAckData> retryDataList = null;
            Consumer consumer = new Consumer(connection.getId(), topic, app, Consumer.ConsumeType.JOYQUEUE);

            for (int i = 0; i < dataList.size(); i++) {
                CommitAckData data = dataList.get(i);
                messageLocations[i] = new MessageLocation(topic, partition, data.getIndex());

                if (!data.getRetryType().equals(RetryType.NONE)) {
                    if (retryDataList == null) {
                        retryDataList = Lists.newLinkedList();
                    }
                    retryDataList.add(data);
                }
            }

            consume.acknowledge(messageLocations, consumer, connection, true);

            if (CollectionUtils.isNotEmpty(retryDataList)) {
                commitRetry(connection, consumer, retryDataList);
            }

            return JoyQueueCode.SUCCESS;
        } catch (JoyQueueException e) {
            logger.error("commit ack exception, topic: {}, app: {}, partition: {}, transport: {}", topic, app, partition, connection.getTransport(), e);
            return JoyQueueCode.valueOf(e.getCode());
        } catch (Exception e) {
            logger.error("commit ack exception, topic: {}, app: {}, partition: {}, transport: {}", topic, app, partition, connection.getTransport(), e);
            return JoyQueueCode.CN_UNKNOWN_ERROR;
        }
    }

    protected void commitRetry(Connection connection, Consumer consumer, List<CommitAckData> data) throws JoyQueueException {
        for (CommitAckData ackData : data) {
            PullResult pullResult = consume.getMessage(consumer, ackData.getPartition(), ackData.getIndex(), 1);
            List<ByteBuffer> buffers = pullResult.getBuffers();

            if (buffers.size() != 1) {
                logger.error("get retryMessage error, message not exist, transport: {}, topic: {}, partition: {}, index: {}",
                        connection.getTransport().remoteAddress(), consumer.getTopic(), ackData.getPartition(), ackData.getIndex());
                continue;
            }

            try {
                ByteBuffer buffer = buffers.get(0);
                BrokerMessage brokerMessage = Serializer.readBrokerMessage(buffer);
                RetryMessageModel model = generateRetryMessage(consumer, brokerMessage, buffer.array(), ackData.getRetryType().name());
                retryManager.addRetry(Lists.newArrayList(model));
            } catch (Exception e) {
                logger.error("add retryMessage exception, transport: {}, topic: {}, partition: {}, index: {}",
                        connection.getTransport().remoteAddress(), consumer.getTopic(), ackData.getPartition(), ackData.getIndex(), e);
            }
        }
    }

    private RetryMessageModel generateRetryMessage(Consumer consumer, BrokerMessage brokerMessage, byte[] brokerMessageData/* BrokerMessage 序列化后的字节数组 */, String exception) {
        RetryMessageModel model = new RetryMessageModel();
        model.setBusinessId(brokerMessage.getBusinessId());
        model.setTopic(consumer.getTopic());
        model.setApp(consumer.getApp());
        model.setPartition(Partition.RETRY_PARTITION_ID);
        model.setIndex(brokerMessage.getMsgIndexNo());
        model.setBrokerMessage(brokerMessageData);
        byte[] exceptionBytes = exception.getBytes(Charset.forName("UTF-8"));
        model.setException(exceptionBytes);
        model.setSendTime(brokerMessage.getStartTime());

        return model;
    }


    @Override
    public int type() {
        return JoyQueueCommandType.COMMIT_ACK_REQUEST.getCode();
    }
}