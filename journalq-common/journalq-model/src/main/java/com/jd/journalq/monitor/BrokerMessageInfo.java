package com.jd.journalq.monitor;

import com.jd.journalq.message.BrokerMessage;

import java.util.Map;

/**
 * BrokerMessageInfo
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2018/11/29
 */
public class BrokerMessageInfo extends BaseMonitorInfo {

    private long msgIndexNo;
    private int storeTime;
    private long startTime;
    private byte source;
    private short partition;
    private String topic;
    private String app;
    private String businessId;
    private byte priority;
    private long bodyCRC;
    private String body;
    private Map<String, String> attributes;

    public BrokerMessageInfo() {

    }

    public BrokerMessageInfo(BrokerMessage message) {
        this.msgIndexNo = message.getMsgIndexNo();
        this.storeTime = message.getStoreTime();
        this.startTime = message.getStartTime();
        this.source = message.getSource();
        this.partition = message.getPartition();
        this.topic = message.getTopic();
        this.app = message.getApp();
        this.businessId = message.getBusinessId();
        this.priority = message.getPriority();
        this.bodyCRC = message.getBodyCRC();
        this.body = message.getText();
        this.attributes = message.getAttributes();
    }

    public long getMsgIndexNo() {
        return msgIndexNo;
    }

    public void setMsgIndexNo(long msgIndexNo) {
        this.msgIndexNo = msgIndexNo;
    }

    public int getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(int storeTime) {
        this.storeTime = storeTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public byte getSource() {
        return source;
    }

    public void setSource(byte source) {
        this.source = source;
    }

    public short getPartition() {
        return partition;
    }

    public void setPartition(short partition) {
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public long getBodyCRC() {
        return bodyCRC;
    }

    public void setBodyCRC(long bodyCRC) {
        this.bodyCRC = bodyCRC;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}