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
package io.chubao.joyqueue.broker.monitor.service.support;

import io.chubao.joyqueue.broker.cluster.ClusterManager;
import io.chubao.joyqueue.domain.TopicConfig;
import io.chubao.joyqueue.domain.TopicName;
import io.chubao.joyqueue.response.BooleanResponse;
import io.chubao.joyqueue.broker.monitor.service.MetadataMonitorService;

/**
 * DefaultMetadataMonitorService
 *
 * author: gaohaoxiang
 * date: 2019/2/11
 */
public class DefaultMetadataMonitorService implements MetadataMonitorService {

    private ClusterManager clusterManager;

    public DefaultMetadataMonitorService(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public TopicConfig getTopicMetadata(String topic, boolean isCluster) {
        TopicName topicName = TopicName.parse(topic);
        if (isCluster) {
            return clusterManager.getTopicConfig(topicName);
        } else {
            return clusterManager.getNameService().getTopicConfig(topicName);
        }
    }

    @Override
    public BooleanResponse getReadableResult(String topic, String app, String address) {
        TopicName topicName = TopicName.parse(topic);
        return clusterManager.checkReadable(topicName, app, address);
    }

    @Override
    public BooleanResponse getWritableResult(String topic, String app, String address) {
        TopicName topicName = TopicName.parse(topic);
        return clusterManager.checkWritable(topicName, app, address);
    }
}