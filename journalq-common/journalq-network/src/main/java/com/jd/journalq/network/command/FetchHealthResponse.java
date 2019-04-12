package com.jd.journalq.network.command;

import com.jd.journalq.network.transport.command.JMQPayload;

/**
 * FetchHealthResponse
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2018/12/28
 */
public class FetchHealthResponse extends JMQPayload {

    private double point;

    public FetchHealthResponse() {

    }

    public FetchHealthResponse(double point) {
        this.point = point;
    }

    @Override
    public int type() {
        return JMQCommandType.FETCH_HEALTH_RESPONSE.getCode();
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public double getPoint() {
        return point;
    }
}