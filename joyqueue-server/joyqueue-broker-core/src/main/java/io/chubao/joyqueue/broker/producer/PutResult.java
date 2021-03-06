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
package io.chubao.joyqueue.broker.producer;

import io.chubao.joyqueue.store.WriteResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lining11
 * Date: 2018/8/30
 */
public class PutResult {

    private Map<Short,WriteResult> writeResults = new HashMap<>();

    public void addWriteResult(Short partition ,WriteResult writeResult){
        writeResults.put(partition,writeResult);
    }

    public Map<Short,WriteResult> getWriteResults(){
        return writeResults;
    }

}
