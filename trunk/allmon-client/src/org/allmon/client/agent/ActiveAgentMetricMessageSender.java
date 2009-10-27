package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

class ActiveAgentMetricMessageSender extends AgentMetricMessageSender {

    ActiveAgentMetricMessageSender(ActiveAgent activeAgent) {
        super(activeAgent);
    }
    
    ActiveAgent getAgent() {
        return (ActiveAgent)super.getAgent();
    }
    
    final void insertPoint(MetricMessage metricMessage) {
        getAgent().addMetricMessage(metricMessage);
    }
    
    
    
}
