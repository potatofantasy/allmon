package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentMetricBufferTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AgentMetricBufferTest.class);
    
    private static final long INTERVAL = 2000;
    private static final long KEEPINGTIME = 50000;
    
    public void test() {
        AgentContext agentContext = new AgentContext();
        
        // first invocation creates and starts buffering process
    	AgentMetricBuffer metricBuffer = new AgentMetricBuffer(agentContext);
        assertEquals(0, metricBuffer.getFlushCount());
        assertEquals(0, metricBuffer.getFlushedItemsCount());
        
        metricBuffer.setFlushingInterval(INTERVAL);
//        metricBuffer.setKeepiengTime(KEEPINGTIME);
        assertEquals(INTERVAL, metricBuffer.getFlushingInterval());
        
        logger.debug("buffer is running in background...");
        
        logger.debug("add something");
        metricBuffer.add(createMessage());
        metricBuffer.add(createMessage());
        metricBuffer.add(createMessage());
        logger.debug("end of adding - now wait...");
        
        sleep(INTERVAL + 200);
        
        assertEquals(1, metricBuffer.getFlushCount());
        assertEquals(3, metricBuffer.getFlushedItemsCount());
        
        logger.debug("add something aggain");
        metricBuffer.add(createMessage());
        metricBuffer.add(createMessage());
        metricBuffer.add(createMessage());
        metricBuffer.add(createMessage());
        logger.debug("and wait...");
        
        sleep(INTERVAL + 200);
        
        assertEquals(2, metricBuffer.getFlushCount());
        assertEquals(7, metricBuffer.getFlushedItemsCount());
        
        logger.debug("force flush");
        metricBuffer.flush();
        
        assertEquals(3, metricBuffer.getFlushCount());
        assertEquals(7, metricBuffer.getFlushedItemsCount());
        
        sleep(INTERVAL);
        assertEquals(4, metricBuffer.getFlushCount());
        
        metricBuffer.flushSendTerminate(); // from this point on buffering thread cannot flush/send anything else
        assertEquals(5, metricBuffer.getFlushCount()); // 5 - because 5th flush is forced
        
        sleep(INTERVAL);
        assertEquals(6, metricBuffer.getFlushCount()); // still 5
        
        agentContext.stop();
        
        logger.debug("the end.");
    }
        
    private static void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static MetricMessage createMessage() {
    	return MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX");
    }
        
}
