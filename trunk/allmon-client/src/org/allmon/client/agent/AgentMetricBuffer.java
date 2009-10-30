package org.allmon.client.agent;

import java.util.List;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AgentMetricBuffer is a singleton. TODO go to multiton!!!
 * 
 * It ensures that:
 * (1) JMS broker is up and listening, 
 * (2) TODO sending messages are buffered and pre-aggregated.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has JmsBrokerSampler 
 * class instantiated for the whole live time.</b>
 * 
 */
public class AgentMetricBuffer extends AbstractMetricBuffer<MetricMessage> {

    // creates singleton instance of JmsBrokerSampler
    static {
        JmsBrokerHealthSampler.getInstance();
    }
    
    private static final Log logger = LogFactory.getLog(AgentMetricBuffer.class);
    
    /**
     * The method bases on JmsBrokerSampler checks implementation, 
     * so the last check can be done maximum 60 seconds ago.
     * 
     * @return true if JMS broker is up and running
     */
    static boolean isJmsBrokerUp() {
        return JmsBrokerHealthSampler.getInstance().isJmsBrokerUp();
    }
    
    private AgentMetricBuffer() {
        setFlushingInterval(AllmonCommonConstants.ALLMON_CLIENT_AGENT_METRICBUFFER_FLUSHINGINTERVAL);
    }
    
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final AgentMetricBuffer instance = new AgentMetricBuffer();
    }
    
    public static AgentMetricBuffer getInstance() {
        return SingletonHolder.instance;
    }
    
    
    public void send(List<MetricMessage> flushingList) {
        
        // do not process further if flushingList is not initialised or empty
        if (flushingList == null || flushingList.size() == 0) {
            return;
        }
        
        // Converting list to MetricMessageWrapper
        MetricMessageWrapper messageWrapper = new MetricMessageWrapper();
        for (int i = 0; i < flushingList.size(); i++) {
            messageWrapper.add(flushingList.get(i));
        }
        
        if (isJmsBrokerUp()) {
            logger.debug("Sending " + flushingList.size() + " metrics in one wrapper object");
            MessageSender messageSender = new MessageSender();
            messageSender.sendMessage(messageWrapper);
        } else {
            // TODO is it enough
            logger.warn("Sending of " + flushingList.size() + " couldn't been performed because JMS Broker instance was not up");
        }
        
    }
    
    public void flushAndTerminate() {
        super.flushAndTerminate();
        JmsBrokerHealthSampler.getInstance().terminateProcess();
    }
    
}
