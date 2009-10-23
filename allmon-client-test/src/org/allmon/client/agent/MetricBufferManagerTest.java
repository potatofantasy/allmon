package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetricBufferManagerTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(MetricBufferManagerTest.class);
    
    public void test() {
        // first invocation creates and starts buffering process
        MetricBufferManager bufferManager = MetricBufferManager.getInstance();
        logger.debug("buffer is running in background...");
        
        bufferManager.add();
        bufferManager.add();
        bufferManager.add();
        
        logger.debug("end of adding.");
        logger.debug("now wait...");
        
        sleep();
        
        logger.debug("add something aggain");
        
        MetricBufferManager.getInstance().add();
        MetricBufferManager.getInstance().add();
        MetricBufferManager.getInstance().add();
        MetricBufferManager.getInstance().add();
        
        logger.debug("and wait...");
        
        sleep();
        
        MetricBufferManager.getInstance();
        
        logger.debug("the end.");
        
    }
    
    private static void sleep() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
