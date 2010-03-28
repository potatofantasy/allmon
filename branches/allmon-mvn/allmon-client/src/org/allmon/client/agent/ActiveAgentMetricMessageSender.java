package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;

class ActiveAgentMetricMessageSender extends AgentMetricMessageSender {

    ActiveAgentMetricMessageSender(ActiveAgent activeAgent) {
        super(activeAgent);
    }

    ActiveAgent getAgent() {
        return (ActiveAgent) super.getAgent();
    }

    final void insertPoint(MetricMessage metricMessage) {
        metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_ENTRY);
        metricMessage.setDurationTime(0);
        getAgent().addMetricMessage(metricMessage);
    }

}