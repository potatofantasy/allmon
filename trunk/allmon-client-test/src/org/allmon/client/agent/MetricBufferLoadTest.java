package org.allmon.client.agent;

import java.util.HashMap;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;
import org.allmon.server.loader.LoadTestedClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetricBufferLoadTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(MetricBufferLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 30;
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    //private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 100;
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 10000;
    // soak test - around 20min
//    private final static long THREADS_COUNT = 50;
//    private final static long STARTING_TIME_MILLIS = 1 * 60 * 1000; // rump-up 1 min
//    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 1000; // (!) 2 calls per1 sec
//    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1200 * (1000 / SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX);
    
    public void testMain() throws InterruptedException {
        logger.debug("m2 - start");
        
        final ActiveAgentMetricBuffer metricBuffer = new ActiveAgentMetricBuffer();
        metricBuffer.setFlushingInterval(700);
        logger.debug("buffer is running in background...");
    	
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        logger.debug("m2 - creating definitions of threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // creating a thread
            Thread t = new Thread(new LoadTestedClass(i, STARTING_TIME_MILLIS) {
                public void runConcurently() {
//                    logger.debug("MetricMessage started");
//                    long t0 = System.nanoTime();
//                    long t1 = System.nanoTime();
                    
                    for (int i = 0; i < SUBSEQUENT_CALLS_IN_THREAD; i++) {
                        MetricMessage metricMessage = 
                            MetricMessageFactory4Test.createClassMessage("className"+i, "methodName", "classNameX", "methodNameX", 1);
                        
                        metricBuffer.add(metricMessage);                     
                        
                        //try {
                        //    Thread.sleep((long)(Math.random() * SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX));
                        //} catch (InterruptedException e) {
                        //}
                        
                    }
                    
//                    long t2 = System.nanoTime();
//                    logger.debug("MetricMessage initialized in " + (t1 - t0)/1000000);
//                    logger.debug("MetricMessage metrics sent in " + (t2 - t1)/1000000);
//                    
//                    logger.debug("MetricMessage end");
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
        
        // wait for flushing
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // force flushing
        metricBuffer.flush();
        
        logger.debug("m2 - end");
        
        long expected = THREADS_COUNT * SUBSEQUENT_CALLS_IN_THREAD;
        long actual = metricBuffer.getFlushedItemsCount();
        assertEquals(expected, actual);
        
        logger.info("Total flushing time (handy for tuning FlushingInterval): " + metricBuffer.getSummaryFlushTime());
        
    }

}