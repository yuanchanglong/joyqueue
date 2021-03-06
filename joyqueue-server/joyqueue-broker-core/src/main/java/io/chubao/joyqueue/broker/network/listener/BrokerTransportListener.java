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
package io.chubao.joyqueue.broker.network.listener;

import io.chubao.joyqueue.broker.helper.SessionHelper;
import io.chubao.joyqueue.broker.monitor.SessionManager;
import io.chubao.joyqueue.network.event.TransportEvent;
import io.chubao.joyqueue.network.event.TransportEventType;
import io.chubao.joyqueue.network.session.Connection;
import io.chubao.joyqueue.toolkit.concurrent.EventListener;

/**
 * BrokerTransportListener
 *
 * author: gaohaoxiang
 * date: 2018/10/10
 */
public class BrokerTransportListener implements EventListener<TransportEvent> {

    private SessionManager sessionManager;

    public BrokerTransportListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void onEvent(TransportEvent event) {
        TransportEventType type = event.getType();
        if (!(type.equals(TransportEventType.CLOSE) || type.equals(TransportEventType.EXCEPTION))) {
            return;
        }
        Connection connection = SessionHelper.getConnection(event.getTransport());
        if (connection == null) {
            return;
        }
        sessionManager.removeConnection(connection.getId());
    }
}