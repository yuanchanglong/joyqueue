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

import io.chubao.joyqueue.broker.election.command.AppendEntriesResponse;
import io.chubao.joyqueue.network.transport.codec.JoyQueueHeader;
import io.chubao.joyqueue.network.transport.codec.PayloadDecoder;
import io.chubao.joyqueue.network.command.CommandType;
import io.chubao.joyqueue.network.transport.command.Type;
import io.netty.buffer.ByteBuf;

/**
 * author: zhuduohui
 * email: zhuduohui@jd.com
 * date: 2018/9/27
 */
public class AppendEntriesResponseDecoder implements PayloadDecoder<JoyQueueHeader>, Type {
    @Override
    public Object decode(final JoyQueueHeader header, final ByteBuf buffer) throws Exception {
        AppendEntriesResponse appendEntriesResponse = new AppendEntriesResponse();

        appendEntriesResponse.setTerm(buffer.readInt());
        appendEntriesResponse.setSuccess(buffer.readBoolean());
        appendEntriesResponse.setNextPosition(buffer.readLong());
        appendEntriesResponse.setWritePosition(buffer.readLong());
        appendEntriesResponse.setReplicaId(buffer.readInt());

        return appendEntriesResponse;
    }

    @Override
    public int type() {
        return CommandType.RAFT_APPEND_ENTRIES_RESPONSE;
    }
}
