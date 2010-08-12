package org.allmon.client.agent.aop.ns;

import org.allmon.client.agent.AgentMetricBufferLoadTest;
import org.allmon.client.agent.aop.HelloWorldImpl;
import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.loadtest.AbstractLoadTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simulates concurrently executed API of simple spring based application.
 * 
 */
public class SpringHigherLoadAppTest extends AbstractLoadTest<HelloWorldImpl, Object> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(SpringHigherLoadAppTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 15;
    private final static long STARTING_TIME_MILLIS = 1 * 10;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 0; //0 - no sleep
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 2000;
    
    private final static String SPRING_CONFIG_LOCATION = "org/allmon/client/agent/aop/ns/spring-config-ns.xml";
    private final ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
	
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
    	HelloWorldImpl bean = (HelloWorldImpl) applicationContext.getBean("messageBean");
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