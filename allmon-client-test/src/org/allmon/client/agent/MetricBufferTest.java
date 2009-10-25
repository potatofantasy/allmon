package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetricBufferTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(MetricBufferTest.class);
    
    private static final long INTERVAL = 1000;
    
    public void test() {
        // first invocation creates and starts buffering process
        MetricBuffer.getInstance();
        assertEquals(0, MetricBuffer.getInstance().getFlushCount());
        assertEquals(0, MetricBuffer.getInstance().getFlushedItemsCount());
        
        MetricBuffer.getInstance().setFlushingInterval(INTERVAL);
        assertEquals(INTERVAL, MetricBuffer.getInstance().getFlushingInterval());
        
        logger.debug("buffer is running in background...");
        
        logger.debug("add something");
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        logger.debug("end of adding - now wait...");
        
        sleep(INTERVAL + 200);
        
        assertEquals(1, MetricBuffer.getInstance().getFlushCount());
        assertEquals(3, MetricBuffer.getInstance().getFlushedItemsCount());
        
        logger.debug("add something aggain");
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        logger.debug("and wait...");
        
        sleep(INTERVAL + 200);
        
        assertEquals(2, MetricBuffer.getInstance().getFlushCount());
        assertEquals(7, MetricBuffer.getInstance().getFlushedItemsCount());
        
        logger.debug("force flush");
        MetricBuffer.getInstance().flush();
        
        assertEquals(3, MetricBuffer.getInstance().getFlushCount());
        assertEquals(7, MetricBuffer.getInstance().getFlushedItemsCount());
        
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
    	return MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000));
    }
    
    private static void createMessageAndAddToBuffer(int c, long sleep) {
    	for (int i = 0; i < c; i++) {
            MetricBuffer.getInstance().add(createMessage());
            if (sleep > 0) {
            	sleep(sleep);
            }
		}
    }
    
    
}
