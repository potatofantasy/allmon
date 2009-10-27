package org.allmon.client.agent;

import java.util.List;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PassiveAgentMetricBuffer extends AbstractMetricBuffer {
    
    // creates singleton instance of JmsBrokerSampler
    static {
        JmsBrokerHealthSampler.getInstance();
    }
    
    private static final Log logger = LogFactory.getLog(PassiveAgentMetricBuffer.class);
        
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
        
        if (flushingList == null || flushingList.size() == 0) {
            return;
        }
        
        MetricMessageWrapper messageWrapper = new MetricMessageWrapper();
        for (int i = 0; i < flushingList.size(); i++) {
            messageWrapper.add((MetricMessage)flushingList.get(i));
        }
        
        if (isJmsBrokerUp()) {
        
            logger.debug("Send " + flushingList.size());
            
            // TODO convert List to MetricMessageWrapper
            MessageSender messageSender = new MessageSender();
            messageSender.sendMessage(messageWrapper);
            
        } else {
            // XXX
        }
        
    }

}