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
package io.openmessaging.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * {@code OMSNamespaceHandler} for the {@code oms} namespace.
 *
 * @version OMS 1.0.0
 * @since OMS 1.0.0
 */
public class OMSNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("access-point", new AccessPointBeanDefinitionParser());
        registerBeanDefinitionParser("producer", new ProducerBeanDefinitionParser());
        registerBeanDefinitionParser("consumer", new ConsumerBeanDefinitionParser());
        registerBeanDefinitionParser("interceptor", new InterceptorBeanDefinitionParser());
    }
}