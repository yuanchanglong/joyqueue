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
package io.chubao.joyqueue.broker.limit.filter;

import io.chubao.joyqueue.broker.BrokerContext;
import io.chubao.joyqueue.broker.BrokerContextAware;
import io.chubao.joyqueue.broker.Plugins;
import io.chubao.joyqueue.broker.helper.AwareHelper;
import io.chubao.joyqueue.broker.limit.LimitRejectedStrategy;
import io.chubao.joyqueue.broker.limit.RateLimitManager;
import io.chubao.joyqueue.broker.limit.RateLimiter;
import io.chubao.joyqueue.broker.limit.config.LimitConfig;
import io.chubao.joyqueue.broker.limit.domain.LimitContext;
import io.chubao.joyqueue.broker.limit.support.DefaultRateLimiterManager;
import io.chubao.joyqueue.broker.network.traffic.Traffic;
import io.chubao.joyqueue.network.transport.Transport;
import io.chubao.joyqueue.network.transport.command.Command;
import io.chubao.joyqueue.network.transport.command.handler.filter.CommandHandlerInvocation;
import io.chubao.joyqueue.network.transport.exception.TransportException;
import io.chubao.joyqueue.toolkit.time.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LimitFilter
 *
 * author: gaohaoxiang
 * date: 2019/5/16
 */
public class LimitFilter extends AbstractLimitFilter implements BrokerContextAware {

    protected static final Logger logger = LoggerFactory.getLogger(LimitFilter.class);

    private LimitConfig config;
    private RateLimitManager rateLimiterManager;
    private LimitRejectedStrategy limitRejectedStrategy;

    @Override
    public Command invoke(CommandHandlerInvocation invocation) throws TransportException {
        if (!config.isEnable()) {
            return invocation.invoke();
        }
        return super.invoke(invocation);
    }

    @Override
    protected boolean limitIfNeeded(String topic, String app, String type, Traffic traffic) {
        RateLimiter rateLimiter = rateLimiterManager.getRateLimiter(topic, app, type);
        if (rateLimiter == null) {
            return false;
        }
        return !rateLimiter.tryAcquireTps() || !rateLimiter.tryAcquireTraffic(traffic.getTraffic(topic));
    }

    @Override
    protected Command doLimit(Transport transport, Command request, Command response) {
        int delay = getDelay(transport, request, response);
        LimitContext limitContext = new LimitContext(transport, request, response, delay);

        if (logger.isDebugEnabled()) {
            logger.debug("traffic limit, transport: {}, request: {}, response: {}, delay: {}", transport, request, response, delay);
        }
        return limitRejectedStrategy.execute(limitContext);
    }

    protected int getDelay(Transport transport, Command request, Command response) {
        int delay = config.getDelay();
        if (delay == LimitConfig.DELAY_DYNAMIC) {
            int dynamicDelay = (int) (1000 - (SystemClock.now() % 1000));
            delay = Math.min(dynamicDelay, config.getMaxDelay());
        }
        return delay;
    }

    @Override
    public void setBrokerContext(BrokerContext brokerContext) {
        this.config = new LimitConfig(brokerContext.getPropertySupplier());
        this.rateLimiterManager = new DefaultRateLimiterManager(brokerContext);
        this.limitRejectedStrategy = AwareHelper.enrichIfNecessary(Plugins.LIMIT_REJECTED_STRATEGY.get(config.getRejectedStrategy()), brokerContext);
    }
}