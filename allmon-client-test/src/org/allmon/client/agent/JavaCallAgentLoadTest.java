package org.allmon.client.agent;

import java.util.HashMap;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;
import org.allmon.server.loader.LoadTestedClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JavaCallAgentLoadTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(JavaCallAgentLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 10; // TODO find out WHY above 5 for 500 calls - sending messages process hangs!!!
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 10;
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1000;
    // soak test - around 20min
//    private final static long THREADS_COUNT = 50;
//    private final static long STARTING_TIME_MILLIS = 1 * 60 * 1000; // rump-up 1 min
//    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 1000; // (!) 2 calls per1 sec
//    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1200 * (1000 / SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX);
    
    public void testMain() throws InterruptedException {
        logger.debug("m2 - start");
        
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        logger.debug("m2 - creating definitions of threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // creating a thread
            Thread t = new Thread(new LoadTestedClass(i, STARTING_TIME_MILLIS) {
                public void runConcurently() {
                    logger.debug("MetricMessage started");
                    long t0 = System.nanoTime();
                    
                    long t1 = System.nanoTime();
                    
                    for (int i = 0; i < SUBSEQUENT_CALLS_IN_THREAD; i++) {
                        MetricMessage metricMessage = 
                            MetricMessageFactory4Test.createClassMessage("className"+i, "methodName", "classNameX", "methodNameX", 1);
                        
                        JavaCallAgent agent = new JavaCallAgent(metricMessage);
                        agent.entryPoint();
                        
                        try {
                            Thread.sleep((long)(Math.random() * SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX));
                        } catch (InterruptedException e) {
                        }
                        
                        agent.exitPoint();
                        
                    }
                    
                    long t2 = System.nanoTime();
                    
                    logger.debug("MetricMessage initialized in " + (t1 - t0)/1000000);
                    logger.debug("MetricMessage metrics sent in " + (t2 - t1)/1000000);
                    
                    logger.debug("MetricMessage end");
                }
            });
            
            loadThreadsMap.put(new Integer(i), t);
        }
        
        logger.debug("m2 - running threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // taking a thread definition to run it
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.start();
        }
        
        logger.debug("m2 - waiting for running threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.join();
        }
        
        // wait to finish flushing all messages
        logger.debug("m2 - waiting for all messages to be flushed");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        
        logger.debug("m2 - end");
    }

}