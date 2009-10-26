package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

public class ActiveAgentMetricMessageSender {

    private final ActiveAgent activeAgent;
    
    ActiveAgentMetricMessageSender(ActiveAgent activeAgent) {
        this.activeAgent = activeAgent;
    }
    
    final void insertPoint(MetricMessage metricMessage) {
        activeAgent.addMetricMessage(metricMessage);
    }
    
    
    
}
