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
    
    public void test() {
        // first invocation creates and starts buffering process
        MetricBuffer.getInstance();
        MetricBuffer.getInstance().setFlushingInterval(1000);
        
        logger.debug("buffer is running in background...");
        
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        
        logger.debug("end of adding.");
        logger.debug("now wait...");
        
        sleep(1500);
        
        logger.debug("add something aggain");
        
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        MetricBuffer.getInstance().add(createMessage());
        
        logger.debug("and wait...");
        
        sleep(1500);
        
        MetricBuffer.getInstance();
        
        logger.debug("the end.");
        
    }
    
    public void testConcurent() {
    	// first invocation creates and starts buffering process
        MetricBuffer.getInstance();
        logger.debug("buffer is running in background...");
    	
        MetricBuffer.getInstance().setFlushingInterval(1000);
        sleep(990);
        
        Thread t = new Thread(new Runnable() {
			public void run() {
				createMessageAndAddToBuffer(100, 1);				
			}
        });
        t.start();
        
        createMessageAndAddToBuffer(100, 1);
        
        sleep(1500);
        
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
