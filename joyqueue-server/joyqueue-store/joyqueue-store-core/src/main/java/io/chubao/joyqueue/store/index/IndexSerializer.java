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
package io.chubao.joyqueue.store.index;

import io.chubao.joyqueue.store.file.LogSerializer;

import java.nio.ByteBuffer;

/**
 * @author liyue25
 * Date: 2018-11-28
 */
public class IndexSerializer implements LogSerializer<IndexItem> {


    @Override
    public IndexItem read(ByteBuffer buffer, int length) {
        return IndexItem.from(buffer);
    }

    @Override
    public int size(IndexItem indexItem) {
        return IndexItem.STORAGE_SIZE;
    }

    @Override
    public int trim(ByteBuffer byteBuffer, int length) {
        return byteBuffer.remaining() - byteBuffer.remaining() % IndexItem.STORAGE_SIZE;
    }

    @Override
    public int append(IndexItem indexItem, ByteBuffer to) {
        indexItem.serializeTo(to);
        return IndexItem.STORAGE_SIZE;
    }

}
