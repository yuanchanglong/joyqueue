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
package io.chubao.joyqueue.event;

import io.chubao.joyqueue.domain.TopicName;

public class TopicEvent extends MetaEvent {
    private TopicName topic;

    public TopicEvent() {
    }

    @Override
    public String getTypeName() {
        return getClass().getTypeName();
    }

    private TopicEvent(EventType type, TopicName topic) {
        super(type);
        this.topic = topic;
    }

    public TopicName getTopic() {
        return topic;
    }

    public void setTopic(TopicName topic) {
        this.topic = topic;
    }

    public static TopicEvent add(TopicName topic) {
        return new TopicEvent(EventType.ADD_TOPIC, topic);
    }

    public static TopicEvent update(TopicName topic) {
        return new TopicEvent(EventType.UPDATE_TOPIC, topic);
    }

    public static TopicEvent remove(TopicName topic) {
        return new TopicEvent(EventType.REMOVE_TOPIC, topic);
    }

    @Override
    public String toString() {
        return "TopicEvent{" +
                "topic='" + topic + '\'' +
                '}';
    }
}
