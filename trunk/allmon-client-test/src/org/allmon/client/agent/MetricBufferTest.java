package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
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
        logger.debug("buffer is running in background...");
        
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        
        logger.debug("end of adding.");
        logger.debug("now wait...");
        
        sleep();
        
        logger.debug("add something aggain");
        
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        MetricBuffer.getInstance().add(MetricMessageFactory.createClassMessage("class1", "m1", "classX", "mX", (long)(Math.random() * 1000)));
        
        logger.debug("and wait...");
        
        sleep();
        
        MetricBuffer.getInstance();
        
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
