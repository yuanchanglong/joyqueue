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
package io.chubao.joyqueue.broker.kafka.handler;

import io.chubao.joyqueue.broker.kafka.KafkaCommandHandler;
import io.chubao.joyqueue.broker.kafka.KafkaCommandType;
import io.chubao.joyqueue.broker.kafka.KafkaContext;
import io.chubao.joyqueue.broker.kafka.KafkaContextAware;
import io.chubao.joyqueue.broker.kafka.KafkaErrorCode;
import io.chubao.joyqueue.broker.kafka.command.InitProducerIdRequest;
import io.chubao.joyqueue.broker.kafka.command.InitProducerIdResponse;
import io.chubao.joyqueue.broker.kafka.coordinator.transaction.TransactionCoordinator;
import io.chubao.joyqueue.broker.kafka.coordinator.transaction.domain.TransactionMetadata;
import io.chubao.joyqueue.broker.kafka.coordinator.transaction.exception.TransactionException;
import io.chubao.joyqueue.broker.kafka.helper.KafkaClientHelper;
import io.chubao.joyqueue.network.transport.Transport;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InitProducerIdRequestHandler
 *
 * author: gaohaoxiang
 * date: 2019/4/4
 */
public class InitProducerIdRequestHandler implements KafkaCommandHandler, Type, KafkaContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(InitProducerIdRequestHandler.class);

    private TransactionCoordinator transactionCoordinator;

    @Override
    public void setKafkaContext(KafkaContext kafkaContext) {
        this.transactionCoordinator = kafkaContext.getTransactionCoordinator();
    }

    @Override
    public Command handle(Transport transport, Command command) {
        InitProducerIdRequest initProducerIdRequest = (InitProducerIdRequest) command.getPayload();
        String clientId = KafkaClientHelper.parseClient(initProducerIdRequest.getClientId());
        InitProducerIdResponse response = null;

        try {
            TransactionMetadata transactionMetadata = transactionCoordinator.handleInitProducer(clientId, initProducerIdRequest.getTransactionId(), initProducerIdRequest.getTransactionTimeout());
            response = new InitProducerIdResponse(KafkaErrorCode.NONE.getCode(), transactionMetadata.getProducerId(), transactionMetadata.getProducerEpoch());
        } catch (TransactionException e) {
            logger.warn("init producerId exception, code: {}, message: {}, request: {}, code: {}", e.getCode(), e.getMessage(), initProducerIdRequest, e.getCode());
            response = new InitProducerIdResponse((short) e.getCode(), InitProducerIdResponse.NO_PRODUCER_ID, InitProducerIdResponse.NO_PRODUCER_EPOCH);
        } catch (Exception e) {
            logger.error("init producerId exception, request: {}", initProducerIdRequest, e);
            response = new InitProducerIdResponse(KafkaErrorCode.exceptionFor(e), InitProducerIdResponse.NO_PRODUCER_ID, InitProducerIdResponse.NO_PRODUCER_EPOCH);
        }

        return new Command(response);
    }

    @Override
    public int type() {
        return KafkaCommandType.INIT_PRODUCER_ID.getCode();
    }
}