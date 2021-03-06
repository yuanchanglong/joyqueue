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
package io.chubao.joyqueue.client.internal.trace.support;

import io.chubao.joyqueue.client.internal.trace.TraceCaller;

import java.util.List;

/**
 * CompositeTraceCaller
 *
 * author: gaohaoxiang
 * date: 2019/1/3
 */
public class CompositeTraceCaller implements TraceCaller {

    private List<TraceCaller> callers;

    public CompositeTraceCaller(List<TraceCaller> callers) {
        this.callers = callers;
    }

    @Override
    public void end() {
        for (TraceCaller caller : callers) {
            caller.end();
        }
    }

    @Override
    public void error() {
        for (TraceCaller caller : callers) {
            caller.error();
        }
    }
}