package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This MetricMessageSender is used for all agents. 
 * 
 * It ensures that:
 * (1) JMS broker is up and listening, 
 * (2) TODO sending messages are buffered and preaggregated.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has JmsBrokerSampler 
 * class instantiated for the whole live time.</b>
 * 
 */
public class AgentsMetricMessageSender extends MetricMessageSender {
    
    // creates singleton instance of JmsBrokerSampler
    static {
        JmsBrokerSampler.getInstance();
    }
    
    private final static Log logger = LogFactory.getLog(AgentsMetricMessageSender.class);
    
    /**
     * The method bases on JmsBrokerSampler checks implementation, 
     * so the last check can be done maximum 60 seconds ago.
     * 
     * @return true if JMS broker is up and running
     */
    static boolean isJmsBrokerUp() {
        return JmsBrokerSampler.getInstance().isJmsBrokerUp();
    }
    
    private String metricsString = "";
    
    public AgentsMetricMessageSender(MetricMessage message) {
        super(message);
        
        if (!isJmsBrokerUp()) {
            metricsString = message.toString();
        }
    }

    public void insertEntryPoint() {
        if (isJmsBrokerUp()) {
            sendEntryPoint();
        } else {
            bufferMetric();
        }
    }

    public void insertExitPoint() {
        if (isJmsBrokerUp()) {
            sendExitPoint(null);
        } else {
            bufferMetric();
        }
    }

    public void insertExitPointException(Exception exception) {
        if (isJmsBrokerUp()) {
            sendExitPoint(exception);
        } else {
            bufferMetric();
        }
    }
    
    private void bufferMetric() {
        // TODO broker is down - buffer metrics locally MetricMessagesBuffer
        logger.warn("JmsBroker is down, metric hasn't been sent: " + metricsString);
    }

}
