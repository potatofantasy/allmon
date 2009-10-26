package org.allmon.client.agent;

import org.allmon.common.MetricMessage;


/**
 * This class let you monitor actual (real) interaction with a system. 
 * Metrics collected using this approach can be used to determine the actual 
 * service-level quality delivered to end-users and to detect errors 
 * or potential performance in the system. <br>
 * - Concrete agents extending this class can be very helpful in troubleshooting 
 * performance problems once they have occurred.<br>
 * - The most important drawback of this method is that something in the system 
 * has to be performed (triggered externally) to take and collect any metrics. 
 * We will have no defined service level if no action is called. 
 * 
 */
abstract class PassiveAgent extends Agent {

    private final PassiveAgentMetricBuffer metricBuffer = new PassiveAgentMetricBuffer();
    
    private PassiveAgentMetricMessageSender messageSender;
    
    private MetricMessage baseMetricMessage;
    
    PassiveAgent(MetricMessage metricMessage) {
        messageSender = new PassiveAgentMetricMessageSender(this);
        baseMetricMessage = metricMessage;
    }
    
    PassiveAgentMetricMessageSender getMetricMessageSender() {
        return messageSender;
    }
    
    // TODO move this implementation to Agent
    void addMetricMessage(MetricMessage metricMessage) {
        metricBuffer.add(metricMessage);
    }
    
    MetricMessage getBaseMetricMessage() {
        return baseMetricMessage;
    }
    
    MetricMessage getBaseMetricMessageCopy() {
        return (MetricMessage)baseMetricMessage.clone();
    }
    
}
