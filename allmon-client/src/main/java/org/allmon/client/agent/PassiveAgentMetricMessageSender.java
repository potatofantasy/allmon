package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains specific for passive agents code used for sending messages, like:<br>
 * - sending additional subsequent metrics points, <br>
 * - calculating duration times and passing them to metrics message.
 * 
 */
class PassiveAgentMetricMessageSender extends AgentMetricMessageSender {
    
    private final static Log logger = LogFactory.getLog(PassiveAgentMetricMessageSender.class);
    
    private long lastTimeCheck;
    
    PassiveAgentMetricMessageSender(PassiveAgent passiveAgent) {
        super(passiveAgent);
    }
    
    PassiveAgent getAgent() {
        return (PassiveAgent)super.getAgent();
    }
    
    final void insertEntryPoint() {
        lastTimeCheck = System.currentTimeMillis();
        MetricMessage metricMessage = getAgent().getBaseMetricMessage();
        metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_ENTRY);
        metricMessage.setDurationTime(0);
        getAgent().addMetricMessage(metricMessage);
    }

    void insertNextPoint() {
        MetricMessage metricMessage = getAgent().getBaseMetricMessageCopy();
        metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_EXIT);
        metricMessage.setDurationTime(getTimeBetweenChecks());
        getAgent().addMetricMessage(metricMessage);
    }
    
    void insertNextPoint(String point) {
        MetricMessage metricMessage = getAgent().getBaseMetricMessageCopy();
        metricMessage.setPoint(point);
        metricMessage.setDurationTime(getTimeBetweenChecks());
        getAgent().addMetricMessage(metricMessage);
    }
    
    void insertNextPoint(String point, Throwable throwable) {
        MetricMessage metricMessage = getAgent().getBaseMetricMessageCopy();
        metricMessage.setPoint(point);
        metricMessage.setThrowable(throwable);
        metricMessage.setDurationTime(getTimeBetweenChecks());
        getAgent().addMetricMessage(metricMessage);
    }
    
    private long getTimeBetweenChecks() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - lastTimeCheck;
        lastTimeCheck = System.currentTimeMillis();
        return time;
    }
    
}
