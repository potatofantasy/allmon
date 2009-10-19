/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * Active agents are used in active monitoring, where metric 
 * collection process is triggered by agent scheduler mechanism.<br><br>
 * 
 * This technique basis on created scripts to simulate an action which 
 * end-user or other functionality would take in the system. Those scripts 
 * continuously monitor at specified intervals for performance and availability 
 * reasons various system metrics.<br>
 * - Active monitoring test calls add (artificial - not real) load to system.<br>
 * - Applying this approach we can have almost constant knowledge about service 
 * level in our system (if something happen we might know about it even before 
 * end-users notice a problem).
 * 
 */
abstract class ActiveAgent extends Agent implements AgentTaskable {

    abstract MetricMessage collectMetrics();
    
    public final void execute() {
        MetricMessage metricMessage = collectMetrics();
        if (metricMessage == null) {
            throw new RuntimeException("MetricMessage hasn't been initialized properly");
        }
        sendMessage(metricMessage);
    }
    
    private void sendMessage(MetricMessage metricMessage) {
        // TODO review creating different explicitly specified MetricMessageSender
        MetricMessageSender metricMessageSender = new SimpleMetricMessageSender(metricMessage);
        metricMessageSender.sendEntryPoint();
    }
    
}
