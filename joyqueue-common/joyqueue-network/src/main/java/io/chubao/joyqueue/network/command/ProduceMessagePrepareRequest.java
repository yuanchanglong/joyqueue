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

import io.chubao.joyqueue.network.transport.command.JoyQueuePayload;

/**
 * ProduceMessagePrepareRequest
 *
 * author: gaohaoxiang
 * date: 2018/12/18
 */
public class ProduceMessagePrepareRequest extends JoyQueuePayload {

    private String topic;
    private String app;
    private long sequence;
    private String transactionId;
    private int timeout;

    @Override
    public int type() {
        return JoyQueueCommandType.PRODUCE_MESSAGE_PREPARE_REQUEST.getCode();
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getApp() {
        return app;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getSequence() {
        return sequence;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }
}