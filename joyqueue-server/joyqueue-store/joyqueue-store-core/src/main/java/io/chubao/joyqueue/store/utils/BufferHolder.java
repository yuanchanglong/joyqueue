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
package io.chubao.joyqueue.store.utils;

/**
 * 可管理的Buffer持有者
 *
 * @author liyue25
 * Date: 2019-03-28
 */
public interface BufferHolder extends Timed {
    /**
     * Buffer大小
     */
    int size();

    /**
     * 是否可以释放？
     */
    boolean isFree();

    /**
     * 尝试释放
     *
     * @return 释放成功返回true，否则返回false
     */
    boolean evict();
}
