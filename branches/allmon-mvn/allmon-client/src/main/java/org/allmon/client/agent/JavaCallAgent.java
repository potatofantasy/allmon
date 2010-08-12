package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;

/**
 * This class can be used to monitor interactions inside a monitored java
 * application.
 * 
 */
public class JavaCallAgent extends PassiveAgent {

    public JavaCallAgent(AgentContext agentContext, MetricMessage metricMessage) {
        super(agentContext, metricMessage);
    }

    public void entryPoint() {
        getMetricMessageSender().insertEntryPoint();
    }

    public void exitPoint() {
        getMetricMessageSender().insertNextPoint();
    }

    public void exitPoint(Throwable throwable) {
        getMetricMessageSender().insertNextPoint(AllmonCommonConstants.METRIC_POINT_EXIT, throwable);
    }

}
