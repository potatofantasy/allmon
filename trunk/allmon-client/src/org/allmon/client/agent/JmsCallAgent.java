package org.allmon.client.agent;

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
        getMetricMessageSender().sendEntryPoint();
    }
    
    public void exitMessageTaken() {
        // TODO MessageSender implementation has to be changed to provide JMS middle step
        getMetricMessageSender().sendExitPoint(null);
    }

    public void exitMessageConsumed() {
        getMetricMessageSender().sendExitPoint(null);
    }

    public void exitMessageConsumed(Exception exception) {
        getMetricMessageSender().sendExitPoint(exception);
    }

}
