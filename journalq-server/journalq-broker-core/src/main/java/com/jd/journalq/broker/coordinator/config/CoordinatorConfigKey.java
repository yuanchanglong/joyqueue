package com.jd.journalq.broker.coordinator.config;

import com.jd.journalq.toolkit.config.PropertyDef;

/**
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2018/12/4
 */
public enum CoordinatorConfigKey implements PropertyDef {

    // 协调者主题
    GROUP_TOPIC_CODE("coordinator.group.topic.code", "__group_coordinators", PropertyDef.Type.STRING),
    // 协调者主题分区
    GROUP_TOPIC_PARTITIONS("coordinator.group.topic.partitions", (short) 1, PropertyDef.Type.SHORT),
    // 协调者过期时间
    GROUP_EXPIRE_TIME("coordinator.group.expire.time", 1000 * 60 * 60 * 1, PropertyDef.Type.INT),
    // 协调者最多group
    GROUP_MAX_NUM("coordinator.group.max.num", 1024 * 10, PropertyDef.Type.INT),

    // 事务协调者主题
    TRANSACTION_TOPIC_CODE("coordinator.transaction.topic.code", "__transaction_coordinators", PropertyDef.Type.STRING),
    // 事务协调者主题分区
    TRANSACTION_TOPIC_PARTITIONS("coordinator.transaction.topic.partitions", (short) 1, PropertyDef.Type.SHORT),
    // 事务过期时间
    TRANSACTION_EXPIRE_TIME("coordinator.transaction.expire.time", 1000 * 60 * 60 * 1, PropertyDef.Type.INT),
    // 事务最多
    TRANSACTION_MAX_NUM("coordinator.transaction.max.num", 1024 * 10, PropertyDef.Type.INT),

    // session同步超时
    SESSION_SYNC_TIMEOUT("coordinator.session.sync.timeout", 1000 * 3, Type.INT),
    // session缓存时间
    SESSION_EXPIRE_TIME("coordinator.session.expire.time", 1000 * 60 * 10, Type.INT),

    ;

    private String name;
    private Object value;
    private PropertyDef.Type type;

    CoordinatorConfigKey(String name, Object value, PropertyDef.Type type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public PropertyDef.Type getType() {
        return type;
    }
}