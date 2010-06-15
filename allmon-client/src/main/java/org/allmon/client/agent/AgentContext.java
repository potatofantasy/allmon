package org.allmon.client.agent;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class instance is necessary to monitor - create agents classes. Object
 * of this class protects live-cycle of all vital for metrics data mechanisms
 * (i.e. sending data to JMS broker, maintaining internal buffers).
 * 
 * After all monitoring work method stop must be called.
 * 
 */
public class AgentContext {

    private static final Log logger = LogFactory.getLog(AgentContext.class);

    private final ConnectionFactory cf; // PooledConnectionFactory pcf;

    private final AgentMetricBuffer metricBuffer;

    public AgentContext() {
        // is using pooled connections
        cf = AllmonActiveMQConnectionFactory.client(); // TODO potentially IoC this object
        metricBuffer = new AgentMetricBuffer(this);
        logger.debug("AgentContext is up...");
    }

    /**
     * This method:
     * <li> kills buffering thread,
     * <li> close connections pool to JMS broker.
     */
    public void stop() {
        // TODO remove instance
        // instance = null;

        // kill buffering thread
        metricBuffer.flushSendTerminate();

        // stopping connection pool to broker
        PooledConnectionFactory pcf = (PooledConnectionFactory) cf;
        // logger.debug(">>> IdleTimeout: " + pcf.getIdleTimeout());
        // logger.debug(">>> MaxConnections:" + pcf.getMaxConnections());
        // logger.debug(">>> MaximumActive:" + pcf.getMaximumActive());
        pcf.stop();

        logger.info("Agent context end.");

    }

    ConnectionFactory getCf() {
        return cf;
    }

    AgentMetricBuffer getMetricBuffer() {
        return metricBuffer;
    }

    String getName() {
        return "AgentContext-" + toString();
    }
    
}
