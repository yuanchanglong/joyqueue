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
package io.chubao.joyqueue.service;

import io.chubao.joyqueue.model.domain.ApplicationToken;
import io.chubao.joyqueue.model.query.QApplicationToken;
import io.chubao.joyqueue.nsr.NsrService;

import java.util.List;

/**
 * Created by yangyang115 on 18-9-6.
 */
public interface ApplicationTokenService extends NsrService<ApplicationToken,QApplicationToken,Long> {

    /**
     * 统计应用令牌次数
     *
     * @param appId
     * @return
     */
    int countByAppId(long appId);

    /**
     * 根据应用的id 查询 应用的token信息
     *
     * @param appId
     * @return
     */
    List<ApplicationToken> findByAppId(long appId);

    /**
     * 根据应用id+token 查询 token信息
     *
     * @param app
     * @param token
     * @return
     */
    ApplicationToken findByAppAndToken(String app, String token);
}
