package org.allmon.client.agent;

import java.util.ArrayList;
import java.util.List;

import org.allmon.client.agent.buffer.AbstractMetricBuffer;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageCumulator;
import org.allmon.common.MetricMessageCumulatorMethod;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AgentMetricBuffer is a singleton. 
 * 
 * TODO Review going to multiton instance per AgentContext!!!
 * 
 * It ensures that:
 * (1) JMS broker is up and listening, 
 * (2) sending messages are buffered and pre-aggregated.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has JmsBrokerSampler 
 * class instantiated for the whole live time.</b>
 * 
 */
class AgentMetricBuffer extends AbstractMetricBuffer<MetricMessage> {

    // TODO review creating instance of JmsBrokerSampler
//    static {
//        JmsBrokerHealthSampler.getInstance();
//    }
    
    private static final Log logger = LogFactory.getLog(AgentMetricBuffer.class);
        
    /**
     * The method bases on JmsBrokerSampler checks implementation, 
     * so the last check can be done maximum 60 seconds ago.
     * 
     * @return true if JMS broker is up and running
     */
    static boolean isJmsBrokerUp() {
        return true; //JmsBrokerHealthSampler.getInstance().isJmsBrokerUp();
    }
    
    private final AgentContext agentContext;
    
    AgentMetricBuffer(AgentContext agentContext) {
        this.agentContext = agentContext;
        setFlushingInterval(AllmonCommonConstants.ALLMON_CLIENT_AGENT_METRICBUFFER_FLUSHINGINTERVAL);
        setVerboseLogging(AllmonCommonConstants.ALLMON_CLIENT_AGENT_METRICBUFFER_VERBOSELOGGING);
    }
    
    // TODO 1. add a mechanism which allows splitting huge MetricMessageWrapper to smaller objects send subsequently 
    // TODO 2. add threshold level which will work as a throttle ("allmon.client.agent.metricbuffer.maxmessages")
    // TODO 3. <parametrize> above points
    // TODO 4. finish a mechanism to calculate (aggregate) MetricMessages with cumulative values of collected figures - MetricMessageCumulatorMethod
    // TODO 5. log warn message if set flushing time is shorter than last sending time!
    public void send(List<MetricMessage> flushingList) {
        // do not process further if flushingList is not initialized or empty
        if (flushingList == null || flushingList.size() == 0) {
            return;
        }
        
        // Converting list to MetricMessageWrapper
        MetricMessageWrapper messageWrapper = new MetricMessageWrapper();
        for (MetricMessage metricMessage : flushingList) {
        	messageWrapper.add(metricMessage);
		}
        
        // Executing cumulative processes on collected metrics data
        List<MetricMessageWrapper> cumulatedWrappers = cumulateMetrics(messageWrapper);
        
        // sending 
    	sendData(cumulatedWrappers);
    }
        
    public void flushSendTerminate() {
        super.flushSendTerminate();
        //JmsBrokerHealthSampler.getInstance().terminateProcess();
    }
    
    private List<MetricMessageWrapper> cumulateMetrics(MetricMessageWrapper messageWrapperAllMetrics) {
    	ArrayList<MetricMessageWrapper> cumulatedWrappers = new ArrayList<MetricMessageWrapper>();
        List<MetricMessageCumulatorMethod> methods = MetricMessageCumulator.getAllMethods(messageWrapperAllMetrics);
    	// for each cumulative method execute a specific cumulator process 
        // and add to list of cumulated wrappers
        for (MetricMessageCumulatorMethod method : methods) {
        	MetricMessageWrapper cumulatedWrapper = 
        		new MetricMessageCumulator(method).execute(messageWrapperAllMetrics);
    		cumulatedWrappers.add(cumulatedWrapper);
		}
    	return cumulatedWrappers;
    }
    
    private void sendData(List<MetricMessageWrapper> wrappers) {
    	for (MetricMessageWrapper messageWrapper : wrappers) {
    		if (isJmsBrokerUp()) {
    			if (isVerboseLogging()) {
    				logger.debug("Sending " + messageWrapper.size() + " metrics in one wrapper object");
                }
    			MessageSender messageSender = new MessageSender(agentContext.getCf());
                messageSender.sendMessage(messageWrapper);
            } else {
                // TODO is it enough?
                logger.warn("Sending of " + messageWrapper.size() + " couldn't been performed because JMS Broker instance was not up");
            }
		}
    }
}
