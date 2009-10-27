package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

class ActiveAgentMetricMessageSender extends AgentMetricMessageSender {

    private final ActiveAgent activeAgent;
    
    ActiveAgentMetricMessageSender(ActiveAgent activeAgent) {
        this.activeAgent = activeAgent;
    }
    
    final void insertPoint(MetricMessage metricMessage) {
        activeAgent.addMetricMessage(metricMessage);
    }
    
    
    
}
