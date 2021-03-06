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
package io.chubao.joyqueue.broker.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.primitives.Shorts;
import io.chubao.joyqueue.broker.BrokerContext;
import io.chubao.joyqueue.broker.cluster.ClusterManager;
import io.chubao.joyqueue.broker.election.ElectionService;
import io.chubao.joyqueue.domain.Broker;
import io.chubao.joyqueue.domain.PartitionGroup;
import io.chubao.joyqueue.exception.JoyQueueCode;
import io.chubao.joyqueue.exception.JoyQueueException;
import io.chubao.joyqueue.network.command.BooleanAck;
import io.chubao.joyqueue.network.transport.Transport;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.Type;
import io.chubao.joyqueue.network.transport.command.handler.CommandHandler;
import io.chubao.joyqueue.network.transport.exception.TransportException;
import io.chubao.joyqueue.nsr.network.command.CreatePartitionGroup;
import io.chubao.joyqueue.nsr.network.command.NsrCommandType;
import io.chubao.joyqueue.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wylixiaobin
 * Date: 2018/10/8
 */
public class CreatePartitionGroupHandler implements CommandHandler, Type {
    private static Logger logger = LoggerFactory.getLogger(CreatePartitionGroupHandler.class);
    private ClusterManager clusterManager;
    private ElectionService electionService;
    private StoreService storeService;
    public CreatePartitionGroupHandler(BrokerContext brokerContext) {
        this.clusterManager = brokerContext.getClusterManager();
        this.electionService = brokerContext.getElectionService();
        this.storeService = brokerContext.getStoreService();
    }

    @Override
    public int type() {
        return NsrCommandType.NSR_CREATE_PARTITIONGROUP;
    }

    @Override
    public Command handle(Transport transport, Command command) throws TransportException {
        if (command == null) {
            logger.error("CreatePartitionGroupHandler request command is null");
            return null;
        }
        CreatePartitionGroup request = ((CreatePartitionGroup)command.getPayload());
        try{
            PartitionGroup group = request.getPartitionGroup();
            if(logger.isDebugEnabled())logger.debug("begin createPartitionGroup topic[{}] partitionGroupRequest [{}] ",group.getTopic(),JSON.toJSONString(request));
            if (!request.isRollback()) {
                commit(group);
            } else {
                rollback(group);
            }
            return BooleanAck.build();
        }catch (JoyQueueException e) {
            logger.error(String.format("CreatePartitionGroupHandler request command[%s] error",request),e);
            return BooleanAck.build(e.getCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(String.format("CreatePartitionGroupHandler request command[%s] error",request),e);
            return BooleanAck.build(JoyQueueCode.CN_UNKNOWN_ERROR,e.getMessage());
        }
    }

    private void commit(PartitionGroup group) throws Exception {
        if(logger.isDebugEnabled())logger.debug("topic[{}] add partitionGroup[{}]",group.getTopic(),group.getGroup());
        //if (!storeService.partitionGroupExists(group.getTopic(),group.getGroup())) {
            storeService.createPartitionGroup(group.getTopic().getFullName(), group.getGroup(), Shorts.toArray(group.getPartitions()));
            //}
            Set<Integer> replicas = group.getReplicas();
            List<Broker> list = new ArrayList<>(replicas.size());
            replicas.forEach(brokerId->{
                list.add(clusterManager.getBrokerById(brokerId));
            });
            electionService.onPartitionGroupCreate(group.getElectType(),group.getTopic(),group.getGroup(),list,group.getLearners(),clusterManager.getBrokerId(),group.getLeader());
    }
    private void rollback(PartitionGroup group){
        if(logger.isDebugEnabled())logger.debug("topic[{}] remove partitionGroup[{}]",group.getTopic(),group.getGroup());
            storeService.removePartitionGroup(group.getTopic().getFullName(),group.getGroup());
            electionService.onPartitionGroupRemove(group.getTopic(),group.getGroup());
    }
}
