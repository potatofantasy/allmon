/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;

/**
 * Active agents are used in active monitoring, where metric 
 * collection process is triggered by agent scheduler mechanism.<br><br>
 * 
 * This technique basis on created scripts to simulate an action which 
 * end-user or other functionality would take in the system. Those scripts 
 * continuously monitor at specified intervals for performance and availability 
 * reasons various system metrics.<br>
 * - Active monitoring test calls add (artificial - additional) load to monitored 
 * system.<br>
 * - Applying this approach we can have almost constant knowledge about service 
 * level in our system (if something happen we might know about it even before 
 * end-users notice a problem).<br>
 * 
 */
abstract class ActiveAgent extends Agent implements AgentTaskable {

    //private final ActiveAgentMetricBuffer metricBuffer = new ActiveAgentMetricBuffer();
    
    private ActiveAgentMetricMessageSender messageSender = new ActiveAgentMetricMessageSender(this);
    
    /**
     * This method force all active agents to contain specific collectMetrics implementations
     * designed to meet different requirements of agents.
     * 
     * @return MetricMessage
     */
    abstract MetricMessageWrapper collectMetrics();
    
    /**
     * This method is used by AgentCallerMain to execute process of:
     * (1) collecting metrics and 
     * (2) sending metrics messages. 
     * This method is final, so no other concrete Agent implementation can override it.
     */
    public final void execute() {
        decodeAgentTaskableParams();
        MetricMessageWrapper metricMessageWrapper = collectMetrics();
        if (metricMessageWrapper == null || metricMessageWrapper.size() == 0) {
            throw new RuntimeException("MetricMessages havent't been initialized properly");
        }
        sendMessage(metricMessageWrapper);
    }
    
    private void sendMessage(MetricMessageWrapper metricMessageWrapper) {
        // TODO review creating different explicitly specified MetricMessageSender
        for (int i = 0; i < metricMessageWrapper.size(); i++) {
            MetricMessage metricMessage = metricMessageWrapper.get(i);
            messageSender.insertPoint(metricMessage);
        }
    }
    
    // TODO move this implementation to Agent
    void addMetricMessage(MetricMessage metricMessage) {
        getMetricBuffer().add(metricMessage); // TODO review for multiton
    }
    
    // TODO review this property - it is forcing to use decodeAgentTaskableParams implementation for all active agents 
    // XXX create a new interface only for those Active agents which should have parameters
    // XXX in future allmon releases parameters for active agents can be specified in XML, so String[] can be not enough
    private String[] paramsString;

    final String getParamsString(int i) {
        if (paramsString != null && paramsString.length > i) {
            return paramsString[i];
        }
        return null; // TODO review is null result is better than throwing an exception
    }
    
    /**
     * Forced by AgentTaskable - in the future can by forced only for specific active agents.
     */
    public final void setParameters(String[] paramsString) {
        //if (paramsString == null) // TODO decide what to do with null paramsString
        this.paramsString = paramsString;
    }
    
    /**
     * Enforce implementation of decoding parameters set by AgentCallerMain
     * for all AgentTaskable classes
     */
    abstract void decodeAgentTaskableParams();
    
    
}
