/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * Active agents are used in active monitoring, where metric 
 * collection process is triggered by agent scheduler mechanism.
 * 
 */
abstract class ActiveAgent extends Agent implements AgentTaskable {

    abstract MetricMessage collectMetrics();
    
    public final void execute() {
        MetricMessage metricMessage = collectMetrics();
        sendMessage(metricMessage);
    }
    
    private void sendMessage(MetricMessage metricMessage) {
        // TODO review creating different explicitly specified MetricMessageSender
        MetricMessageSender metricMessageSender = new SimpleMetricMessageSender(metricMessage);
        metricMessageSender.sendEntryPoint();
    }
    
}
