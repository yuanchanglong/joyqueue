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
package io.chubao.joyqueue.network.command;

import io.chubao.joyqueue.domain.TopicConfig;
import io.chubao.joyqueue.network.transport.command.JoyQueuePayload;

import java.util.List;

/**
 * @author wylixiaobin
 * Date: 2018/10/10
 */
public class SubscribeAck extends JoyQueuePayload {
    private List<TopicConfig> topicConfigs;

    public SubscribeAck topicConfigs(List<TopicConfig> topicConfigs) {
        this.topicConfigs = topicConfigs;
        return this;
    }

    @Override
    public int type() {
        return JoyQueueCommandType.MQTT_SUBSCRIBE_ACK.getCode();
    }


    public List<TopicConfig> getTopicConfigs() {
        return topicConfigs;
    }

    public void setTopicConfigs(List<TopicConfig> topicConfigs) {
        this.topicConfigs = topicConfigs;
    }

    @Override
    public String toString() {
        return "SubscribeAck{" +
                "topicConfigs=" + topicConfigs +
                '}';
    }
}
