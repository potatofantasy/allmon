package org.allmon.client.agent.aop.aspectj;

import org.allmon.client.agent.aop.HelloWorldImpl;
import org.allmon.common.AbstractLoadTest;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simulates concurrently executed API of simple pojo application.
 * 
 * Go to PojoHelloWorldAppTest.java to read more about necessary AspectJ configuration.
 */
public class PojoHigherLoadAppTest extends AbstractLoadTest<HelloWorldImpl, Object> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(PojoHigherLoadAppTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 3;
    private final static long STARTING_TIME_MILLIS = 1 * 10;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 0; //0 - no sleep
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 2000;
    
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 3000);
                
//        long expected = THREADS_COUNT * SUBSEQUENT_CALLS_IN_THREAD;
//        long actual = metricBuffer.getFlushedItemsCount();
//        assertEquals(expected, actual);
                
//        logger.info("Total flushing time (handy for tuning FlushingInterval): " + metricBuffer.getSummaryFlushTime());
    }
    
    public HelloWorldImpl initialize() {
    	HelloWorldImpl bean = new HelloWorldImpl();
    	bean.setSilentMode(true); // no system out
        return bean;
    }
    
    public Object preCall(int thread, int iteration, HelloWorldImpl bean) {
	    bean.printMessage("something to print");      
        return null;
    }
    
    public void postCall(Object preCallParameters) {
    }
    
}