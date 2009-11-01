package org.allmon.client.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a singleton. Its instance is necessary 
 * to monitor - create agents classes. Object of this class 
 * protects live-cycle of all vital for metrics data mechanisms
 * (i.e. sending data to JMS broker, maintaining internal buffers). 
 * 
 * After all monitoring work method stop must be called.
 * 
 */
public class AgentContext {

	private static final Log logger = LogFactory.getLog(AgentContext.class);
    
	private AgentContext() {
	}
	
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final AgentContext instance = new AgentContext();
    }
    
    public static AgentContext getInstance() {
        return SingletonHolder.instance;
    }
    
    /**
     * This method:
     * - kills buffering thread,
     * - close connections pool to JMS broker.
     */
	public void stop() {
		// TODO remove instance
		//instance = null;
		
		// kill buffering thread
        JavaCallAgent.getMetricBuffer().flushAndTerminate();
        
        // stopping connection pool to broker
        MessageSender.stop();
        
        logger.info("Agent context end.");
        
	}
	
}
