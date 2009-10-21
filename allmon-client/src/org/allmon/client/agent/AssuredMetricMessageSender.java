package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

public class AssuredMetricMessageSender extends MetricMessageSender {

    public AssuredMetricMessageSender(MetricMessage message) {
        super(message);
    }

    public void insertEntryPoint() {
        sendEntryPoint();
    }

    public void insertExitPoint() {
        sendExitPoint(null);
    }

    public void insertExitPointException(Exception exception) {
        sendExitPoint(exception);
    }

}
