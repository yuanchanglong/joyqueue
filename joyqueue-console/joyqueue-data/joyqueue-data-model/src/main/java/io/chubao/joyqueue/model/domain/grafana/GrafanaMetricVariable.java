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
package io.chubao.joyqueue.model.domain.grafana;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Grafana metric variable
 * author: chenyanying3
 * email: chenyanying3@jd.com
 * date: 2019/02/29
 */
public class GrafanaMetricVariable {

    private String name;
    private String target;
    @JacksonXmlElementWrapper(localName = "metrics")
    @JacksonXmlProperty(localName = "metric")
    private List<GrafanaMetric> metrics;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<GrafanaMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<GrafanaMetric> metrics) {
        this.metrics = metrics;
    }
}
