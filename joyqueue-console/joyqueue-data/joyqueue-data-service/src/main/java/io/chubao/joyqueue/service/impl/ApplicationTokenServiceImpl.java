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
package io.chubao.joyqueue.service.impl;

import com.alibaba.fastjson.JSON;
import io.chubao.joyqueue.model.PageResult;
import io.chubao.joyqueue.model.QPageQuery;
import io.chubao.joyqueue.exception.ServiceException;
import io.chubao.joyqueue.model.domain.Application;
import io.chubao.joyqueue.model.domain.ApplicationToken;
import io.chubao.joyqueue.model.domain.Identity;
import io.chubao.joyqueue.model.query.QApplicationToken;
import io.chubao.joyqueue.service.ApplicationService;
import io.chubao.joyqueue.service.ApplicationTokenService;
import io.chubao.joyqueue.nsr.AppTokenNameServerService;
import io.chubao.joyqueue.token.TokenSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by yangyang115 on 18-9-6.
 */
@Service("applicationTokenService")
public class ApplicationTokenServiceImpl implements ApplicationTokenService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationTokenServiceImpl.class);

    @Resource
    private AppTokenNameServerService appTokenNameServerService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    TokenSupplier tokenSupplier;

    @Override
    public int countByAppId(final long appId) {
        return findByAppId(appId).size();
    }

    @Override
    public int add(final ApplicationToken v) {
        if (v == null) {
            return 0;
        }
        v.setToken(tokenSupplier.apply(v.getApplication().getCode(), v.getEffectiveTime(), v.getExpirationTime()));
        v.initializeTime();
        if (v.getCreateTime() == null) {
            v.setCreateTime(new Date());
        }
        v.setUpdateTime(v.getCreateTime());
        try {
            appTokenNameServerService.add(v);
        } catch (Exception e) {
            String errorMsg = String.format("add naming service failed,%s", JSON.toJSONString(v));
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);//回滚
        }
        return 1;
    }

    @Override
    public List<ApplicationToken> findByAppId(final long appId) {
        Application application = applicationService.findById(appId);
        try {
            return appTokenNameServerService.findByQuery(new QApplicationToken(new Identity(application.getCode()),null));
        } catch (Exception e) {
            throw new RuntimeException("findByAppAndToken,exception",e);
        }
    }

    @Override
    public ApplicationToken findByAppAndToken(String app, String token) {
//        return repository.findByAppAndToken(app,token);
        try {
            List<ApplicationToken> appTokenList = appTokenNameServerService.findByQuery(new QApplicationToken(new Identity(app),token));
            if (appTokenList != null && appTokenList.size() > 0) {
                return appTokenList.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("findByAppAndToken", e);//回滚
        }
    }

    @Override
    public ApplicationToken findById(Long id) {
        try {
            return appTokenNameServerService.findById(id);
        } catch (Exception e) {
            throw new ServiceException(ServiceException.IGNITE_RPC_ERROR,e.getMessage());
        }
    }

    @Override
    public int update(ApplicationToken model) {
        try {
            appTokenNameServerService.update(model);
        } catch (Exception e) {
            String errorMsg = String.format("update naming service failed,%s", JSON.toJSONString(model));
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);//回滚
        }
        return 1;
    }

    @Override
    public List<ApplicationToken> findByQuery(QApplicationToken query) throws Exception {
        return appTokenNameServerService.findByQuery(query);
    }

    @Override
    public int delete(ApplicationToken model) {
        try {
            appTokenNameServerService.delete(model);
        } catch (Exception e) {
            String errorMsg = String.format("update naming service failed,%s", JSON.toJSONString(model));
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);//回滚
        }
        return 1;
    }

//    @Override
//    public List<ApplicationToken> findByQuery(ListQuery<QApplicationToken> query) {
//        try {
//            QApplicationToken qApplicationToken = null;
//            String app = null;
//            String token = null;
//            if (query != null) {
//                qApplicationToken = query.getQuery();
//                if (qApplicationToken != null){
//                   token = qApplicationToken.getToken();
//                   if (qApplicationToken.getApplication() != null) {
//                       app = qApplicationToken.getApplication().getCode();
//                   }
//                }
//            }
//            return appTokenNameServerService.findByAppAndToken(app,token);
//        } catch (Exception e) {
//            logger.error("findByAppAndToken error",e);
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public PageResult<ApplicationToken> findByQuery(QPageQuery<QApplicationToken> query) {
        try {
           return appTokenNameServerService.findByQuery(query);
        } catch (Exception e) {
            throw new RuntimeException("findByQueryAppToken exption",e);
        }
    }
}
