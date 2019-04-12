package com.jd.journalq.network.command;

import com.jd.journalq.network.transport.command.JMQPayload;

import java.util.Map;

/**
 * AddConsumerResponse
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2018/12/10
 */
public class AddConsumerResponse extends JMQPayload {

    private Map<String, String> consumerIds;

    @Override
    public int type() {
        return JMQCommandType.ADD_CONSUMER_RESPONSE.getCode();
    }

    public void setConsumerIds(Map<String, String> consumerIds) {
        this.consumerIds = consumerIds;
    }

    public Map<String, String> getConsumerIds() {
        return consumerIds;
    }
}