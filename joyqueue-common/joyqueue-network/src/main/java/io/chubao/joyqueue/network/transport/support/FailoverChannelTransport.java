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
package io.chubao.joyqueue.network.transport.support;

import io.chubao.joyqueue.network.event.TransportEvent;
import io.chubao.joyqueue.network.event.TransportEventType;
import io.chubao.joyqueue.network.transport.ChannelTransport;
import io.chubao.joyqueue.network.transport.TransportAttribute;
import io.chubao.joyqueue.network.transport.TransportClient;
import io.chubao.joyqueue.network.transport.TransportState;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.CommandCallback;
import io.chubao.joyqueue.network.transport.config.TransportConfig;
import io.chubao.joyqueue.network.transport.exception.TransportException;
import io.chubao.joyqueue.toolkit.concurrent.EventBus;
import io.chubao.joyqueue.toolkit.network.IpUtil;
import io.chubao.joyqueue.toolkit.retry.RetryPolicy;
import io.chubao.joyqueue.toolkit.time.SystemClock;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * FailoverChannelTransport
 *
 * author: gaohaoxiang
 * date: 2018/9/3
 */
public class FailoverChannelTransport implements ChannelTransport {

    protected static final Logger logger = LoggerFactory.getLogger(FailoverChannelTransport.class);

    private volatile ChannelTransport delegate;
    private SocketAddress address;
    private long connectionTimeout;
    private TransportClient transportClient;
    private TransportConfig config;
    private EventBus<TransportEvent> transportEventBus;
    private volatile long lastReconnect;
    private volatile long lastRequest;

    public FailoverChannelTransport(ChannelTransport delegate, SocketAddress address, long connectionTimeout,
                                    TransportClient transportClient, TransportConfig config,
                                    EventBus<TransportEvent> transportEventBus) {
        this.delegate = delegate;
        this.address = address;
        this.connectionTimeout = connectionTimeout;
        this.transportClient = transportClient;
        this.config = config;
        this.transportEventBus = transportEventBus;
    }

    @Override
    public Channel getChannel() {
        return delegate.getChannel();
    }

    @Override
    public Command sync(Command command) throws TransportException {
        return sync(command, 0);
    }

    @Override
    public Command sync(Command command, long timeout) throws TransportException {
        RetryPolicy retryPolicy = config.getRetryPolicy();
        TransportException lastException = null;
        Command response = null;
        int retryTimes = 0;

        for (int i = 0, retryLimit = retryPolicy.getMaxRetrys(); i <= retryLimit; i++) {
            try {
                response = delegate.sync(command, timeout);
                lastRequest = SystemClock.now();
                break;
            } catch (TransportException e) {
                // 如果不是超时异常或者超过最大成功请求时间，那么尝试重连
                if (!(e instanceof TransportException.RequestTimeoutException)
                        || (retryPolicy.getMaxRetryDelay() > 0 && SystemClock.now() - lastRequest > retryPolicy.getMaxRetryDelay())) {

                    if (!tryReconnect()) {
                        // 重连失败，抛出异常
                        throw e;
                    }
                }

                lastException = e;
                retryTimes++;
            }
        }

        // 如果有过异常，并且没有重试成功，抛出异常
        if (lastException != null && response == null) {
            throw lastException;
        }

        // 有过重试，打印日志
        if (lastException != null) {
            if (logger.isWarnEnabled()) {
                logger.warn("transport sync exception, retry {} times success, command: {}, timeout: {}", retryTimes, command, timeout, lastException);
            }
        }

        return response;
    }

    @Override
    public void async(Command command, CommandCallback callback) throws TransportException {
        async(command, 0, callback);
    }

    @Override
    public void async(final Command command, final long timeout, final CommandCallback callback) throws TransportException {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        if (!checkChannel()) {
            callback.onException(command, TransportException.RequestErrorException.build(IpUtil.toAddress(address)));
            return;
        }
        delegate.async(command, timeout, callback);
    }

    @Override
    public CompletableFuture<?> async(Command command) throws TransportException {
        return delegate.async(command);
    }

    @Override
    public CompletableFuture<?> async(Command command, long timeout) throws TransportException {
        return delegate.async(command, timeout);
    }

    @Override
    public void oneway(Command command) throws TransportException {
        oneway(command, 0);
    }

    @Override
    public void oneway(Command command, long timeout) throws TransportException {
        delegate.oneway(command, timeout);
    }

    @Override
    public void acknowledge(Command request, Command response) throws TransportException {
        delegate.acknowledge(request, response);
    }

    @Override
    public void acknowledge(Command request, Command response, CommandCallback callback) throws TransportException {
        delegate.acknowledge(request, response, callback);
    }

    @Override
    public SocketAddress remoteAddress() {
        return delegate.remoteAddress();
    }

    @Override
    public TransportAttribute attr() {
        return delegate.attr();
    }

    @Override
    public void attr(TransportAttribute attribute) {
        delegate.attr(attribute);
    }

    @Override
    public TransportState state() {
        return delegate.state();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    protected boolean checkChannel() {
        if (isChannelActive()) {
            return true;
        }
        return tryReconnect();
    }

    protected boolean tryReconnect() {
        if (!isNeedReconnect()) {
            return false;
        }
        synchronized (this) {
            // 判断重连间隔
            if (isNeedReconnect()) {
                return reconnect();
            } else {
                return false;
            }
        }
    }

    protected boolean isChannelActive() {
        return delegate.getChannel().isActive();
    }

    protected boolean isNeedReconnect() {
        return SystemClock.now() - lastReconnect > config.getRetryPolicy().getRetryDelay();
    }

    protected boolean reconnect() {
        try {
            ChannelTransport newTransport = (ChannelTransport) transportClient.createTransport(address, connectionTimeout);
            ChannelTransport delegate = this.delegate;
            this.delegate = newTransport;
            try {
                delegate.stop();
            } catch (Throwable t) {
                logger.warn("stop transport exception, transport: {}", newTransport, t);
            }

            if (logger.isInfoEnabled()) {
                logger.info("reconnect transport success, transport: {}", newTransport);
            }

            transportEventBus.add(new TransportEvent(TransportEventType.RECONNECT, newTransport));
            return true;
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("reconnect transport exception, address: {}", address, t);
            }
            return false;
        } finally {
            lastReconnect = SystemClock.now();
        }
    }
}