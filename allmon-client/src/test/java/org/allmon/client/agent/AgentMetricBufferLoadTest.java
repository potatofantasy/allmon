package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;
import org.allmon.common.loadtest.AbstractLoadTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentMetricBufferLoadTest extends AbstractLoadTest<AgentMetricBuffer, Object> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AgentMetricBufferLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 30;
    private final static long STARTING_TIME_MILLIS = 1 * 10;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 0; // no sleep
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 3000;
    
    private final AgentContext agentContext = new AgentContext();
    private final AgentMetricBuffer metricBuffer = new AgentMetricBuffer(agentContext);
    
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 3000);
        
        // force flushing
        metricBuffer.flush();
        
        long expected = THREADS_COUNT * SUBSEQUENT_CALLS_IN_THREAD;
        long actual = metricBuffer.getFlushedItemsCount();
        assertEquals(expected, actual);
        
        agentContext.stop();
        
        logger.info("Total flushing time (handy for tuning FlushingInterval): " + metricBuffer.getSummaryFlushTime());
    }
    
    public AgentMetricBuffer initialize() {
        metricBuffer.setFlushingInterval(500);
        logger.debug("buffer is running in background...");
        return metricBuffer;
    }
    
    public Object preCall(int thread, int iteration, AgentMetricBuffer metricBuffer) {
        MetricMessage metricMessage = MetricMessageFactory4Test.createClassMessage(
                    "className" + iteration, "methodName", "classNameX", "methodNameX");
        metricBuffer.add(metricMessage);                     
        return null;
    }
    
    public void postCall(Object preCallParameters) {
    }
        
}