package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;

class ActiveAgentMetricMessageSender //extends AgentMetricMessageSender 
{
	private final ActiveAgent agent;
	
    ActiveAgentMetricMessageSender(ActiveAgent activeAgent) {
        //super(activeAgent);
    	this.agent = activeAgent;
    }

    ActiveAgent getAgent() {
        return agent;
    }

    final void insertPoint(MetricMessage metricMessage) {
        metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_ENTRY);
        metricMessage.setDurationTime(0);
        getAgent().addMetricMessage(metricMessage);
    }

}