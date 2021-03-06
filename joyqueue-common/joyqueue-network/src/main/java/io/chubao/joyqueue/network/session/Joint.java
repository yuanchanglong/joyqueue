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
package io.chubao.joyqueue.network.session;

/**
 * 接入
 */
public class Joint {
    // 主题
    protected String topic;
    // 消费者
    protected String app;

    public Joint() {
    }

    public Joint(String topic, String app) {
        this.topic = topic;
        this.app = app;
    }

    public static Joint create(String topic, String app) {
        return new Joint(topic, app);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Joint joint = (Joint) o;

        if (app != null ? !app.equals(joint.app) : joint.app != null) {
            return false;
        }
        if (topic != null ? !topic.equals(joint.topic) : joint.topic != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + (app != null ? app.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Joint{" +
                "topic='" + topic + '\'' +
                ", app='" + app + '\'' +
                '}';
    }
}
