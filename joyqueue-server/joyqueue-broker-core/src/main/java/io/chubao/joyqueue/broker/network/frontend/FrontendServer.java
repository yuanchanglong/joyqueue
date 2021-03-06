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
package io.chubao.joyqueue.broker.network.frontend;

import io.chubao.joyqueue.broker.BrokerContext;
import io.chubao.joyqueue.broker.network.backend.BrokerExceptionHandler;
import io.chubao.joyqueue.broker.network.protocol.MultiProtocolTransportServerFactory;
import io.chubao.joyqueue.broker.network.protocol.ProtocolManager;
import io.chubao.joyqueue.network.event.TransportEvent;
import io.chubao.joyqueue.network.transport.TransportServer;
import io.chubao.joyqueue.network.transport.TransportServerFactory;
import io.chubao.joyqueue.network.transport.command.handler.ExceptionHandler;
import io.chubao.joyqueue.network.transport.config.ServerConfig;
import io.chubao.joyqueue.toolkit.concurrent.EventBus;
import io.chubao.joyqueue.toolkit.concurrent.EventListener;
import io.chubao.joyqueue.toolkit.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FrontendServer
 *
 * author: gaohaoxiang
 * date: 2018/9/17
 */
public class FrontendServer extends Service {

    protected static final Logger logger = LoggerFactory.getLogger(FrontendServer.class);

    private ServerConfig config;
    private BrokerContext brokerContext;
    private ProtocolManager protocolManager;
    private EventBus<TransportEvent> transportEventBus;
    private ExceptionHandler exceptionHandler;
    private TransportServerFactory transportServerFactory;
    private TransportServer transportServer;

    public FrontendServer(ServerConfig config, BrokerContext brokerContext, ProtocolManager protocolManager) {
        this.config = config;
        this.brokerContext = brokerContext;
        this.protocolManager = protocolManager;
        this.transportEventBus = new EventBus<>("joyqueue-frontend-eventBus");
        this.exceptionHandler = new BrokerExceptionHandler();
        this.transportServerFactory = new MultiProtocolTransportServerFactory(protocolManager, brokerContext, transportEventBus, exceptionHandler);
    }

    public void addListener(EventListener<TransportEvent> listener) {
        transportEventBus.addListener(listener);
    }

    public void removeListener(EventListener<TransportEvent> listener) {
        transportEventBus.removeListener(listener);
    }

    @Override
    protected void doStart() throws Exception {
        transportEventBus.start();
        transportServer = transportServerFactory.bind(config, config.getHost(), config.getPort());
        transportServer.start();
        logger.info("frontend server is started, host: {}, port: {}", config.getHost(), config.getPort());
    }

    @Override
    protected void doStop() {
        transportEventBus.stop();
        transportServer.stop();
    }
}