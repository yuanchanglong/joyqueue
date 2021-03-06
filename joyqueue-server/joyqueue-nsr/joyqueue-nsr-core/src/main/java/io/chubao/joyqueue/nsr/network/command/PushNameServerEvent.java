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
package io.chubao.joyqueue.nsr.network.command;

import io.chubao.joyqueue.event.NameServerEvent;
import io.chubao.joyqueue.network.transport.command.JoyQueuePayload;

/**
 * @author wylixiaobin
 * Date: 2019/2/17
 */
public class PushNameServerEvent extends JoyQueuePayload {
    private NameServerEvent event;
    public PushNameServerEvent event(NameServerEvent event){
        this.event = event;
        return this;
    }

    public NameServerEvent getEvent() {
        return event;
    }

    @Override
    public int type() {
        return NsrCommandType.PUSH_NAMESERVER_EVENT;
    }

    @Override
    public String toString() {
        return "PushNameServerEvent{" +
                "event=" + event +
                '}';
    }
}
