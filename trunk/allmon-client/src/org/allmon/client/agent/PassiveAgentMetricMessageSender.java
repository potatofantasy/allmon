package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This MetricMessageSender is used for all agents. 
 * 
 * It ensures that:
 * (1) JMS broker is up and listening, 
 * (2) TODO sending messages are buffered and pre-aggregated.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has JmsBrokerSampler 
 * class instantiated for the whole live time.</b>
 * 
 */
class PassiveAgentMetricMessageSender extends AgentMetricMessageSender {
    
    private final static Log logger = LogFactory.getLog(PassiveAgentMetricMessageSender.class);
    
    private final PassiveAgent passiveAgent;
    
    private long lastTimeCheck;
    
    PassiveAgentMetricMessageSender(PassiveAgent passiveAgent) {
        this.passiveAgent = passiveAgent;
    }
    
    final void insertEntryPoint() {
        lastTimeCheck = System.currentTimeMillis();
        MetricMessage metricMessage = passiveAgent.getBaseMetricMessage();
        passiveAgent.addMetricMessage(metricMessage);
    }

    void insertNextPoint() {
        MetricMessage metricMessage = passiveAgent.getBaseMetricMessageCopy();
        metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_EXIT);
        metricMessage.setDurationTime(getTimeBetweenChecks());
        passiveAgent.addMetricMessage(metricMessage);
    }
    
    void insertNextPoint(String point) {
        MetricMessage metricMessage = passiveAgent.getBaseMetricMessageCopy();
        metricMessage.setPoint(point);
        metricMessage.setDurationTime(getTimeBetweenChecks());
        passiveAgent.addMetricMessage(metricMessage);
    }

    void insertNextPoint(String point, Exception exception) {
        MetricMessage metricMessage = passiveAgent.getBaseMetricMessageCopy();
        metricMessage.setPoint(point);
        metricMessage.setException(exception);
        metricMessage.setDurationTime(getTimeBetweenChecks());
        passiveAgent.addMetricMessage(metricMessage);
    }
    
    private long getTimeBetweenChecks() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - lastTimeCheck;
        lastTimeCheck = System.currentTimeMillis();
        return time;
    }

}
