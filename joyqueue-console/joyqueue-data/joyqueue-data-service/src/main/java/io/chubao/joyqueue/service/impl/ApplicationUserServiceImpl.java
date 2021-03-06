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

import io.chubao.joyqueue.model.domain.ApplicationUser;
import io.chubao.joyqueue.model.domain.Identity;
import io.chubao.joyqueue.model.query.QApplicationUser;
import io.chubao.joyqueue.repository.ApplicationUserRepository;
import io.chubao.joyqueue.service.ApplicationUserService;
import org.springframework.stereotype.Service;

/**
 * 应用-用户关联关系 服务
 * Created by chenyanying3 on 2018-10-15
 */
@Service("applicationUserService")
public class ApplicationUserServiceImpl extends PageServiceSupport<ApplicationUser, QApplicationUser, ApplicationUserRepository> implements ApplicationUserService {

    @Override
    public ApplicationUser findByUserApp(String user, String app){
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setApplication(new Identity(app));
        applicationUser.setUser(new Identity(user));
        return repository.findByUserApp(applicationUser);
    }

    @Override
    public int deleteByAppId(long appId) {
        return repository.deleteByAppId(appId);
    }
}
