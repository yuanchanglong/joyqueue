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
package io.chubao.joyqueue.domain;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wylixiaobin
 * Date: 2018/8/17
 */
public class Broker implements Serializable {
    public static final String DEFAULT_RETRY_TYPE="RemoteRetry";
    public static final String PERMISSION_FULL="FULL";
    public static final String PERMISSION_READ="READ";
    public static final String PERMISSION_WRITE="WRITE";
    public static final String PERMISSION_NONE="NONE";
    public static final String DEFAULT_PERMISSION=PERMISSION_FULL;
    protected int id;
    /**
     * Broker实例的ip
     */
    @NotNull(message = "The ip can not be null")
    protected String ip;
    /**
     * Broker实例的端口号
     */
    @NotNull(message = "The port can not be null")
    @Min(value = 100, message = "Please enter 100 to 65535")
    @Max(value = 65535, message = "Please enter 100 to 65535")
    protected int port;
    // 数据中心
    protected String dataCenter;
    /**
     * 重试类型
     */
    protected String retryType = DEFAULT_RETRY_TYPE;

    /**
     * 权限
     * default FULL
     */

    protected PermissionEnum permission = PermissionEnum.FULL;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    @Transient
    public String getAddress() {
        return ip + ":" + port;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    public String getRetryType() {
        return retryType;
    }

    public void setRetryType(String retryType) {
        this.retryType = retryType;
    }

    public PermissionEnum getPermission() {
        return permission;
    }

    public void setPermission(PermissionEnum permission) {
        this.permission = permission;
    }

    /**
     * @return 选举，复制都使用这个端口
     */
    //TODO 是否可以删掉
    @Transient
    public int getBackEndPort() {
        return port + 1;
    }

    /**
     * @return 监控服务端口
     * BrokerManage
     */
    @Transient
    public int getMonitorPort() {
        return port + 2;
    }

    /**
     * @return nameServer manager api port
     * BrokerManage
     */
    @Transient
    public int getManagerPort() {
        return port + 3;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Broker broker = (Broker) o;
        return id == broker.id &&
                port == broker.port &&
                Objects.equals(ip, broker.ip);
    }

    public enum PermissionEnum {
        FULL(0,"FULL"),
        READ(1,"READ"),
        WRITE(2,"WRITE"),
        NONE(3,"NONE");

        private int value;
        private String name;

        PermissionEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static PermissionEnum value(String permission) {
            for (PermissionEnum permissionEnum:PermissionEnum.values()){
                if (permissionEnum.name.equals(permission)) {
                    return permissionEnum;
                }
            }
            return PermissionEnum.FULL;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, port);
    }

    @Override
    public String toString() {
        return "Broker{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", dataCenter='" + dataCenter + '\'' +
                ", retryType=" + retryType +
                ", permission=" + permission +
                '}';
    }
}

