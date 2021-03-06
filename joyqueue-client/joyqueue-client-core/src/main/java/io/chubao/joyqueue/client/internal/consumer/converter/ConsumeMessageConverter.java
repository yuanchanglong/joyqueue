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
package io.chubao.joyqueue.client.internal.consumer.converter;

import com.google.common.collect.Lists;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeMessage;
import io.chubao.joyqueue.client.internal.consumer.domain.ConsumeReply;
import io.chubao.joyqueue.network.command.RetryType;

import java.util.List;

/**
 * ConsumeMessageConverter
 *
 * author: gaohaoxiang
 * date: 2019/1/11
 */
public class ConsumeMessageConverter {

    public static List<ConsumeReply> convertToReply(List<ConsumeMessage> messages, RetryType retryType) {
        List<ConsumeReply> result = Lists.newArrayListWithCapacity(messages.size());
        for (ConsumeMessage message : messages) {
            result.add(new ConsumeReply(message.getPartition(), message.getIndex(), retryType));
        }
        return result;
    }
}