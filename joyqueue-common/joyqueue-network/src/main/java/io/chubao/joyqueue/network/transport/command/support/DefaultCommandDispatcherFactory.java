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
package io.chubao.joyqueue.network.transport.command.support;

import io.chubao.joyqueue.network.transport.command.CommandDispatcher;
import io.chubao.joyqueue.network.transport.command.CommandDispatcherFactory;
import io.chubao.joyqueue.network.transport.command.handler.CommandHandlerFactory;
import io.chubao.joyqueue.network.transport.command.handler.ExceptionHandler;
import io.chubao.joyqueue.network.transport.command.handler.filter.CommandHandlerFilterFactory;
import io.chubao.joyqueue.network.protocol.ExceptionHandlerProvider;
import io.chubao.joyqueue.network.protocol.Protocol;
import io.chubao.joyqueue.network.transport.RequestBarrier;
import io.chubao.joyqueue.network.transport.config.TransportConfig;

/**
 * DefaultCommandDispatcherFactory
 *
 * author: gaohaoxiang
 * date: 2018/8/16
 */
public class DefaultCommandDispatcherFactory implements CommandDispatcherFactory {

    private TransportConfig transportConfig;
    private RequestBarrier requestBarrier;
    private CommandHandlerFilterFactory commandHandlerFilterFactory;
    private ExceptionHandler exceptionHandler;

    public DefaultCommandDispatcherFactory(TransportConfig transportConfig, RequestBarrier requestBarrier, CommandHandlerFilterFactory commandHandlerFilterFactory, ExceptionHandler exceptionHandler) {
        this.transportConfig = transportConfig;
        this.requestBarrier = requestBarrier;
        this.commandHandlerFilterFactory = commandHandlerFilterFactory;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public CommandDispatcher getCommandDispatcher(Protocol protocol) {
        ExceptionHandler exceptionHandler = getExceptionHandler(protocol);
        CommandHandlerFactory commandHandlerFactory = protocol.createCommandHandlerFactory();
        RequestHandler requestHandler = new RequestHandler(commandHandlerFactory, commandHandlerFilterFactory, exceptionHandler);
        ResponseHandler responseHandler = new ResponseHandler(transportConfig, requestBarrier, exceptionHandler);
        return new DefaultCommandDispatcher(requestBarrier, requestHandler, responseHandler);
    }

    protected ExceptionHandler getExceptionHandler(Protocol protocol) {
        if (protocol instanceof ExceptionHandlerProvider) {
            return ((ExceptionHandlerProvider) protocol).getExceptionHandler();
        } else {
            return this.exceptionHandler;
        }
    }
}