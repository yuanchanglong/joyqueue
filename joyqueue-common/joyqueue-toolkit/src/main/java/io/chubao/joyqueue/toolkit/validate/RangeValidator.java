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
package io.chubao.joyqueue.toolkit.validate;

import io.chubao.joyqueue.toolkit.validate.annotation.Range;

import java.lang.annotation.Annotation;

/**
 * 验证不能为空，支持字符序列，集合，散列，数组
 * Created by hexiaofeng on 16-5-10.
 */
public class RangeValidator implements Validator {

    public static final RangeValidator INSTANCE = new RangeValidator();

    @Override
    public void validate(final Object target, final Annotation annotation, final Value value) throws ValidateException {
        Range range = (Range) annotation;
        Long result = null;
        if (value.value != null) {
            if (value.value instanceof Number) {
                result = ((Number) value.value).longValue();
            }
        }
        if (result == null || result < range.min() || result > range.max()) {
            if (range.message() == null || range.message().isEmpty()) {
                throw new ValidateException(
                        String.format("%s is not in range[%d,%d]", value.name, range.min(), range.max()));
            }
            throw new ValidateException(String.format(range.message(), value.name, range.min(), range.max()));
        }
    }
}
