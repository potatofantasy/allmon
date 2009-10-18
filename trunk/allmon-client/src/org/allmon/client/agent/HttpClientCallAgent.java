package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * This class is used to send client-side metrics to allmon collector service.
 * <br><br>
 * 
 * This agent needs additional HTTP server service to handle coming 
 * from monitored application client-side metrics. Client side code has to be 
 * instrumented, in case of web application page tagging is necessary, 
 * for other client side technologies appropriate instrumentation is needed.
 * 
 */
public class HttpClientCallAgent extends PassiveAgent {
    
    public HttpClientCallAgent(MetricMessage metricMessage) {
        super(metricMessage);
    }
    
    public void dataSentToClient() {
        getMetricMessageSender().sendEntryPoint();
    }
    
    public void dataReceivedByClient() {
        // TODO MessageSender implementation has to be changed to provide JMS middle step
        getMetricMessageSender().sendExitPoint(null);
    }

    public void dataReceivedByClientEnd() {
        // TODO MessageSender implementation has to be changed to provide JMS middle step
        getMetricMessageSender().sendExitPoint(null);
    }
    
    public void requestSent() {
        getMetricMessageSender().sendExitPoint(null);
    }

    public void requestReceived() {
        getMetricMessageSender().sendExitPoint(null);
    }
    
    public void requestReceived(Exception exception) {
        getMetricMessageSender().sendExitPoint(exception);
    }

}