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
package io.chubao.joyqueue.broker.election.network.codec;

import com.alibaba.fastjson.JSON;
import io.chubao.joyqueue.broker.consumer.model.ConsumePartition;
import io.chubao.joyqueue.broker.consumer.position.model.Position;
import io.chubao.joyqueue.broker.election.command.ReplicateConsumePosRequest;
import io.chubao.joyqueue.network.command.CommandType;
import io.chubao.joyqueue.network.serializer.Serializer;
import io.chubao.joyqueue.network.transport.codec.JoyQueueHeader;
import io.chubao.joyqueue.network.transport.codec.PayloadEncoder;
import io.chubao.joyqueue.network.transport.command.Type;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * author: zhuduohui
 * email: zhuduohui@jd.com
 * date: 2018/9/29
 */
public class ReplicateConsumePosRequestEncoder implements PayloadEncoder<ReplicateConsumePosRequest>, Type {
    @Override
    public void encode(final ReplicateConsumePosRequest request, ByteBuf buffer) throws Exception {
        Map<ConsumePartition, Position> consumePositions = request.getConsumePositions();
        int bodyLength = Serializer.INT_SIZE;

        if (request.getHeader().getVersion() == JoyQueueHeader.VERSION_V1) {
            bodyLength = Serializer.SHORT_SIZE;
        }

        if (consumePositions == null) {
            Serializer.write((String) null, buffer, bodyLength);
        } else {
            Serializer.write(JSON.toJSONString(request.getConsumePositions()), buffer, bodyLength);
        }
    }

    @Override
    public int type() {
        return CommandType.REPLICATE_CONSUME_POS_REQUEST;
    }
}
