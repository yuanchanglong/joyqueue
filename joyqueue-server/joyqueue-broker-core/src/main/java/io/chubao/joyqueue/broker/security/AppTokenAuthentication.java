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
package io.chubao.joyqueue.broker.security;

import com.google.common.base.Preconditions;
import io.chubao.joyqueue.broker.BrokerContext;
import io.chubao.joyqueue.broker.BrokerContextAware;
import io.chubao.joyqueue.broker.cluster.ClusterManager;
import io.chubao.joyqueue.domain.AppToken;
import io.chubao.joyqueue.exception.JoyQueueCode;
import io.chubao.joyqueue.exception.JoyQueueException;
import io.chubao.joyqueue.response.BooleanResponse;
import io.chubao.joyqueue.security.Authentication;
import io.chubao.joyqueue.security.PasswordEncoder;
import io.chubao.joyqueue.security.UserDetails;
import io.chubao.joyqueue.toolkit.time.SystemClock;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wylixiaobin
 * Date: 2019/1/21
 */
public class AppTokenAuthentication implements Authentication, BrokerContextAware {
    public static final String DEFAULT_ADMIN_USER="joyqueue";
    private String admin = DEFAULT_ADMIN_USER;
    private ClusterManager clusterManager;

    public AppTokenAuthentication() {
    }

    public AppTokenAuthentication(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public AppTokenAuthentication(ClusterManager clusterManager, String admin) {
        this(clusterManager);
        this.admin = admin;
    }

    @Override
    public UserDetails getUser(String user) throws JoyQueueException {
        return null;
    }

    @Override
    public PasswordEncoder getPasswordEncode() {
        return null;
    }

    @Override
    public BooleanResponse auth(String userName, String password) {
        AppToken appToken = clusterManager.getAppToken(userName, password);
        if (null == appToken) {
            return BooleanResponse.failed(JoyQueueCode.CN_AUTHENTICATION_ERROR);
        }
        long now = SystemClock.now();
        if (now < appToken.getEffectiveTime().getTime() || now > appToken.getExpirationTime().getTime()) {
            return BooleanResponse.failed(JoyQueueCode.CN_AUTHENTICATION_ERROR);
        }
        return BooleanResponse.success();
    }

    @Override
    public BooleanResponse auth(String userName, String password, boolean checkAdmin) {
        BooleanResponse response = auth(userName, password);
        if (response.isSuccess() && (checkAdmin?isAdmin(userName):true)) return response;
        return BooleanResponse.failed(JoyQueueCode.CN_AUTHENTICATION_ERROR);
    }

    @Override
    public boolean isAdmin(String userName) {
        if (StringUtils.isBlank(userName)) return false;
        return userName.equals(admin);
    }

    @Override
    public void setBrokerContext(BrokerContext brokerContext) {
        this.clusterManager = brokerContext.getClusterManager();
        Preconditions.checkArgument(clusterManager != null, "cluster manager can not be null");
        String adminUser = clusterManager.getConfig().getAdminUser();
        if(StringUtils.isNotBlank(adminUser))this.admin = adminUser;
    }
}
