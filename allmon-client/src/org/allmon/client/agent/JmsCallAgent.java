package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;

/**
 * This class is used for JMS (also MDB) passive monitoring.
 * 
 */
public class JmsCallAgent extends PassiveAgent {
    
    public JmsCallAgent(MetricMessage metricMessage) {
        super(metricMessage);
    }
    
    public void entryMessageSent() {
        getMetricMessageSender().insertEntryPoint();
    }
    
    public void exitMessageTaken() {
        // TODO MessageSender implementation has to be changed to provide JMS middle step
        getMetricMessageSender().insertNextPoint(AllmonCommonConstants.METRIC_POINT_TAKEN);
    }

    public void exitMessageConsumed() {
        getMetricMessageSender().insertNextPoint(AllmonCommonConstants.METRIC_POINT_CONSUMED);
    }

    public void exitMessageConsumed(Exception exception) {
        getMetricMessageSender().insertNextPoint(AllmonCommonConstants.METRIC_POINT_CONSUMED, exception);
    }

}
