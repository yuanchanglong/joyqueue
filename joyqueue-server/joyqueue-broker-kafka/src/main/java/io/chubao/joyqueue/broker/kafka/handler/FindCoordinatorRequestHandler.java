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


import io.chubao.joyqueue.broker.kafka.KafkaCommandType;
import io.chubao.joyqueue.broker.kafka.KafkaContext;
import io.chubao.joyqueue.broker.kafka.KafkaContextAware;
import io.chubao.joyqueue.broker.kafka.KafkaErrorCode;
import io.chubao.joyqueue.broker.kafka.command.FindCoordinatorRequest;
import io.chubao.joyqueue.broker.kafka.command.FindCoordinatorResponse;
import io.chubao.joyqueue.broker.kafka.coordinator.CoordinatorType;
import io.chubao.joyqueue.broker.kafka.coordinator.group.GroupCoordinator;
import io.chubao.joyqueue.broker.kafka.coordinator.transaction.TransactionCoordinator;
import io.chubao.joyqueue.broker.kafka.helper.KafkaClientHelper;
import io.chubao.joyqueue.broker.kafka.model.KafkaBroker;
import io.chubao.joyqueue.domain.Broker;
import io.chubao.joyqueue.domain.Subscription;
import io.chubao.joyqueue.network.transport.Transport;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.nsr.NameService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FindCoordinatorRequestHandler
 *
 * author: gaohaoxiang
 * date: 2018/11/5
 */
public class FindCoordinatorRequestHandler extends AbstractKafkaCommandHandler implements KafkaContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(FindCoordinatorRequestHandler.class);

    private GroupCoordinator groupCoordinator;
    private TransactionCoordinator transactionCoordinator;
    private NameService nameService;

    @Override
    public void setKafkaContext(KafkaContext kafkaContext) {
        this.groupCoordinator = kafkaContext.getGroupCoordinator();
        this.transactionCoordinator = kafkaContext.getTransactionCoordinator();
        this.nameService = kafkaContext.getBrokerContext().getNameService();
    }

    @Override
    public Command handle(Transport transport, Command command) {
        FindCoordinatorRequest findCoordinatorRequest = (FindCoordinatorRequest) command.getPayload();
        CoordinatorType coordinatorType = ObjectUtils.defaultIfNull(findCoordinatorRequest.getCoordinatorType(), CoordinatorType.GROUP);
        String coordinatorKey = findCoordinatorRequest.getCoordinatorKey();
        if (coordinatorType.equals(CoordinatorType.TRANSACTION)) {
            coordinatorKey = findCoordinatorRequest.getClientId();
        }
        coordinatorKey = KafkaClientHelper.parseClient(coordinatorKey);

        if (StringUtils.isBlank(coordinatorKey)) {
            logger.warn("coordinatorKey error, coordinatorKey: {}", coordinatorKey);
            return new Command(new FindCoordinatorResponse(KafkaErrorCode.INVALID_GROUP_ID.getCode(), KafkaBroker.INVALID));
        }

        Broker coordinator = null;
        if (coordinatorType.equals(CoordinatorType.GROUP)) {
            if (!nameService.hasSubscribe(coordinatorKey, Subscription.Type.CONSUMPTION)) {
                logger.warn("find subscribe for coordinatorKey {}, subscribe not exist", coordinatorKey);
                return new Command(new FindCoordinatorResponse(KafkaErrorCode.GROUP_AUTHORIZATION_FAILED.getCode(), KafkaBroker.INVALID));
            }
            coordinator = groupCoordinator.findCoordinator(coordinatorKey);
        } else if (coordinatorType.equals(CoordinatorType.TRANSACTION)) {
            if (!nameService.hasSubscribe(coordinatorKey, Subscription.Type.PRODUCTION)) {
                logger.warn("find subscribe for coordinatorKey {}, subscribe not exist", coordinatorKey);
                return new Command(new FindCoordinatorResponse(KafkaErrorCode.TRANSACTIONAL_ID_AUTHORIZATION_FAILED.getCode(), KafkaBroker.INVALID));
            }
            coordinator = transactionCoordinator.findCoordinator(coordinatorKey);
        }

        if (coordinator == null) {
            logger.error("find coordinator for coordinatorKey {}, coordinator is null", coordinatorKey);
            return new Command(new FindCoordinatorResponse(KafkaErrorCode.COORDINATOR_NOT_AVAILABLE.getCode(), KafkaBroker.INVALID));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("find coordinator for coordinatorKey {}, broker: {id: {}, ip: {}, port: {}}", coordinatorKey, coordinator.getId(), coordinator.getIp(), coordinator.getPort());
        }

        FindCoordinatorResponse response = new FindCoordinatorResponse(KafkaErrorCode.NONE.getCode(),
                new KafkaBroker(coordinator.getId(), coordinator.getIp(), coordinator.getPort()));
        return new Command(response);
    }

    @Override
    public int type() {
        return KafkaCommandType.FIND_COORDINATOR.getCode();
    }
}