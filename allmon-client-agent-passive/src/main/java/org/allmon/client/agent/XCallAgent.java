package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;

/**
 * External systems/technologies calls agent.
 * 
 * NOTE: under design
 * 
 */
public class XCallAgent extends PassiveAgent {

    public XCallAgent(AgentContext agentContext, MetricMessage metricMessage) {
        super(agentContext, metricMessage);
    }

    public void entryPoint() {
        getMetricMessageSender().insertEntryPoint();
    }

    public void exitPoint() {
        getMetricMessageSender().insertNextPoint();
    }

    public void exitPoint(Exception exception) {
        getMetricMessageSender().insertNextPoint(AllmonCommonConstants.METRIC_POINT_EXIT, exception);
    }
    
}
