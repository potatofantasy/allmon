package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;

/**
 * This class can be used to monitor interactions inside a monitored java
 * application.
 * 
 */
public class JavaCallAgent extends PassiveAgent {

    public JavaCallAgent(MetricMessage metricMessage) {
        super(metricMessage);
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
