package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.buffer.AbstractMetricBuffer;
import org.allmon.common.AbstractLoadTest;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractBufferLoadTest extends AbstractLoadTest<AbstractMetricBuffer<String>, Object> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AgentMetricBufferLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 30;
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 0; // no sleep
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 10000;
    
    private final AbstractMetricBuffer<String> metricBuffer = new AbstractMetricBuffer<String>() {
        public void send(List<String> flushingList) {
            logger.debug("Sending items: " + flushingList.size());
        }
    };
    
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 3000);
        
        // force flushing
        metricBuffer.flush();
        
        long expected = THREADS_COUNT * SUBSEQUENT_CALLS_IN_THREAD;
        long actual = metricBuffer.getFlushedItemsCount();
        assertEquals(expected, actual);
        
        logger.info("Total flushing time (handy for tuning FlushingInterval): " + metricBuffer.getSummaryFlushTime());
    }
    
    public AbstractMetricBuffer<String> initialize() {
        metricBuffer.setFlushingInterval(500);
        logger.debug("buffer is running in background...");
        return metricBuffer;
    }
    
    public Object preCall(int thread, int iteration, AbstractMetricBuffer<String> metricBuffer) {
        String string = "string" + iteration; 
        metricBuffer.add(string);   
        return null;
    }
    
    public void postCall(Object preCallParameters) {
    }
    
}
