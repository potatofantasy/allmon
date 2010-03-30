package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * Registering listeners for JMX notifications using potentially 
 * Spring's JMX support.
 * 
 * TODO create registered listener which use agent to send acquired metrics data to allmon server
 * 
 */
public class JmxNotificationListenerAgent extends PassiveAgent {

    public JmxNotificationListenerAgent(AgentContext agentContext, MetricMessage metricMessage) {
        super(agentContext, metricMessage);
    }

    public void entryPoint() {
        getMetricMessageSender().insertEntryPoint();
    }
    
}
