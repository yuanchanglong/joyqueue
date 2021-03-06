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
package io.chubao.joyqueue.broker.manage.service.support;

import io.chubao.joyqueue.broker.coordinator.CoordinatorService;
import io.chubao.joyqueue.broker.coordinator.group.GroupMetadataManager;
import io.chubao.joyqueue.broker.manage.service.CoordinatorManageService;

/**
 * DefaultCoordinatorManageService
 *
 * author: gaohaoxiang
 * date: 2018/12/4
 */
public class DefaultCoordinatorManageService implements CoordinatorManageService {

    private CoordinatorService coordinatorService;

    public DefaultCoordinatorManageService(CoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    @Override
    public boolean initCoordinator() {
        return coordinatorService.getCoordinator().initCoordinator();
    }

    @Override
    public boolean removeCoordinatorGroup(String namespace, String groupId) {
        GroupMetadataManager groupMetadataManager = coordinatorService.getOrCreateGroupMetadataManager(namespace);
        return groupMetadataManager.removeGroup(groupId);
    }
}