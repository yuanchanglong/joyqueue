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
package io.chubao.joyqueue.network.codec;

import io.chubao.joyqueue.network.command.FetchProduceFeedbackRequest;
import io.chubao.joyqueue.network.command.JoyQueueCommandType;
import io.chubao.joyqueue.network.command.TxStatus;
import io.chubao.joyqueue.network.serializer.Serializer;
import io.chubao.joyqueue.network.transport.codec.JoyQueueHeader;
import io.chubao.joyqueue.network.transport.codec.PayloadCodec;
import io.chubao.joyqueue.network.transport.command.Type;
import io.netty.buffer.ByteBuf;

/**
 * FetchProduceFeedbackRequestCodec
 *
 * author: gaohaoxiang
 * date: 2018/12/19
 */
public class FetchProduceFeedbackRequestCodec implements PayloadCodec<JoyQueueHeader, FetchProduceFeedbackRequest>, Type {

    @Override
    public FetchProduceFeedbackRequest decode(JoyQueueHeader header, ByteBuf buffer) throws Exception {
        FetchProduceFeedbackRequest fetchProduceFeedbackRequest = new FetchProduceFeedbackRequest();
        fetchProduceFeedbackRequest.setApp(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        fetchProduceFeedbackRequest.setTopic(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        fetchProduceFeedbackRequest.setStatus(TxStatus.valueOf(buffer.readByte()));
        fetchProduceFeedbackRequest.setLongPollTimeout(buffer.readInt());
        return fetchProduceFeedbackRequest;
    }

    @Override
    public void encode(FetchProduceFeedbackRequest payload, ByteBuf buffer) throws Exception {
        Serializer.write(payload.getApp(), buffer, Serializer.SHORT_SIZE);
        Serializer.write(payload.getTopic(), buffer, Serializer.SHORT_SIZE);
        buffer.writeByte(payload.getStatus().getType());
        buffer.writeInt(payload.getLongPollTimeout());
    }

    @Override
    public int type() {
        return JoyQueueCommandType.FETCH_PRODUCE_FEEDBACK_REQUEST.getCode();
    }
}