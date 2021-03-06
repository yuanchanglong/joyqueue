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
package io.chubao.joyqueue.client.internal.consumer.support;

import io.chubao.joyqueue.client.internal.cluster.ClusterClientManager;
import io.chubao.joyqueue.client.internal.cluster.ClusterManager;
import io.chubao.joyqueue.client.internal.consumer.MessagePoller;
import io.chubao.joyqueue.client.internal.consumer.config.ConsumerConfig;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeMessage;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeReply;
import io.chubao.joyqueue.client.internal.consumer.transport.ConsumerClientManager;
import io.chubao.joyqueue.client.internal.metadata.domain.TopicMetadata;
import io.chubao.joyqueue.client.internal.nameserver.NameServerConfig;
import io.chubao.joyqueue.exception.JoyQueueCode;
import io.chubao.joyqueue.toolkit.service.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * MessagePollerWrapper
 *
 * author: gaohaoxiang
 * date: 2018/12/27
 */
public class MessagePollerWrapper extends Service implements MessagePoller {

    private ConsumerConfig consumerConfig;
    private NameServerConfig nameServerConfig;
    private ClusterManager clusterManager;
    private ClusterClientManager clusterClientManager;
    private ConsumerClientManager consumerClientManager;
    private MessagePoller delegate;

    public MessagePollerWrapper(ConsumerConfig consumerConfig, NameServerConfig nameServerConfig, ClusterManager clusterManager,
                                ClusterClientManager clusterClientManager, ConsumerClientManager consumerClientManager, MessagePoller delegate) {
        this.consumerConfig = consumerConfig;
        this.nameServerConfig = nameServerConfig;
        this.clusterManager = clusterManager;
        this.clusterClientManager = clusterClientManager;
        this.consumerClientManager = consumerClientManager;
        this.delegate = delegate;
    }

    @Override
    protected void doStart() throws Exception {
        if (clusterClientManager != null) {
            clusterClientManager.start();
        }
        if (clusterManager != null) {
            clusterManager.start();
        }
        if (consumerClientManager != null) {
            consumerClientManager.start();
        }
        delegate.start();
    }

    @Override
    protected void doStop() {
        delegate.stop();
        if (consumerClientManager != null) {
            consumerClientManager.stop();
        }
        if (clusterManager != null) {
            clusterManager.stop();
        }
        if (clusterClientManager != null) {
            clusterClientManager.stop();
        }
    }

    @Override
    public ConsumeMessage pollOnce(String topic) {
        return delegate.pollOnce(topic);
    }

    @Override
    public ConsumeMessage pollOnce(String topic, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollOnce(topic, timeout, timeoutUnit);
    }

    @Override
    public List<ConsumeMessage> poll(String topic) {
        return delegate.poll(topic);
    }

    @Override
    public List<ConsumeMessage> poll(String topic, long timeout, TimeUnit timeoutUnit) {
        return delegate.poll(topic, timeout, timeoutUnit);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollAsync(String topic) {
        return delegate.pollAsync(topic);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollAsync(String topic, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollAsync(topic, timeout, timeoutUnit);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition) {
        return delegate.pollPartitionOnce(topic, partition);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionOnce(topic, partition, timeout, timeoutUnit);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition, long index) {
        return delegate.pollPartitionOnce(topic, partition, index);
    }

    @Override
    public ConsumeMessage pollPartitionOnce(String topic, short partition, long index, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionOnce(topic, partition, index, timeout, timeoutUnit);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition) {
        return delegate.pollPartition(topic, partition);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartition(topic, partition, timeout, timeoutUnit);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition, long index) {
        return delegate.pollPartition(topic, partition, index);
    }

    @Override
    public List<ConsumeMessage> pollPartition(String topic, short partition, long index, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartition(topic, partition, index, timeout, timeoutUnit);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition) {
        return delegate.pollPartitionAsync(topic, partition);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionAsync(topic, partition, timeout, timeoutUnit);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition, long index) {
        return delegate.pollPartitionAsync(topic, partition, index);
    }

    @Override
    public CompletableFuture<List<ConsumeMessage>> pollPartitionAsync(String topic, short partition, long index, long timeout, TimeUnit timeoutUnit) {
        return delegate.pollPartitionAsync(topic, partition, index, timeout, timeoutUnit);
    }

    @Override
    public JoyQueueCode reply(String topic, List<ConsumeReply> replyList) {
        return delegate.reply(topic, replyList);
    }

    @Override
    public JoyQueueCode replyOnce(String topic, ConsumeReply reply) {
        return delegate.replyOnce(topic, reply);
    }

    @Override
    public TopicMetadata getTopicMetadata(String topic) {
        return delegate.getTopicMetadata(topic);
    }
}