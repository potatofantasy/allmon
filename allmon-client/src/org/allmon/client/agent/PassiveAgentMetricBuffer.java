package org.allmon.client.agent;

import java.io.Serializable;
import java.util.List;

public class PassiveAgentMetricBuffer extends AbstractMetricBuffer {
    
    // creates singleton instance of JmsBrokerSampler
    static {
        JmsBrokerHealthSampler.getInstance();
    }
    
    /**
     * The method bases on JmsBrokerSampler checks implementation, 
     * so the last check can be done maximum 60 seconds ago.
     * 
     * @return true if JMS broker is up and running
     */
    static boolean isJmsBrokerUp() {
        return JmsBrokerHealthSampler.getInstance().isJmsBrokerUp();
    }
    
    public void sendMetrics(List flushingList) {

        if (!isJmsBrokerUp()) {
        
            System.out.println("Send!!!!!!" + flushingList.size());
            
            // TODO convert List to MetricMessageWrapper
            Serializable messageObject = null;
            
            MessageSender messageSender = new MessageSender();
            messageSender.sendMessage(messageObject);
            
        } else {
            // XXX
        }
        
    }

}