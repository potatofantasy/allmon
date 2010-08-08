package org.allmon.common;

import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a very simple generic load test case.
 * 
 * @param <InitParam>
 * @param <PreCallParam>
 */
public abstract class AbstractLoadTest<InitParam, PreCallParam> extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AbstractLoadTest.class);
    
    /**
     * Initialise object(s) before iterating
     * 
     * @return
     */
    public abstract InitParam initialize();
    
    /**
     * First method in an iteration
     * 
     * @param thread
     * @param iteration
     * @param initParameters
     * @return
     */
    public abstract PreCallParam preCall(int thread, int iteration, InitParam initParameters);
    
    /**
     * Second method in an iteration
     * 
     * @param preCallParameters
     */
    public abstract void postCall(PreCallParam preCallParameters);
    
    //public abstract void ending(OP initParameters, PCP preCallParameters);
    
    /**
     * 
     * 
     * @param threadCount 
     * @param startingTimeMills 
     * @param maxSleepBetweenPreAndPostCall it is a maximum time slept between preCall and postCall in an iteration
     * @param subsequentCallsInThread
     * @param sleepAfterTest
     */
    public void runLoadTest(long threadCount, long startingTimeMills, 
            final long maxSleepBetweenPreAndPostCall, final long subsequentCallsInThread, 
            long sleepAfterTest) throws InterruptedException {
        logger.info("runLoadTest - start");
        
        logger.info("load test will execute " + threadCount * subsequentCallsInThread + " calls in " + threadCount + " independent threads");
        logger.info("calls per thread: " + subsequentCallsInThread);
        logger.info("rump up period is (all thread should run in): " + startingTimeMills + "ms");
        logger.info("active part of load test should take: " + ((double)maxSleepBetweenPreAndPostCall / 2 * subsequentCallsInThread / 1000) + "sec");
        logger.info("whole load test should take: " + (((double)sleepAfterTest + maxSleepBetweenPreAndPostCall / 2 * subsequentCallsInThread) / 1000) + "sec");
        
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        long t0 = System.currentTimeMillis();
        
        logger.info("runLoadTest - creating definitions of threads");
        for (int i = 0; i < threadCount; i++) {
            // creating a thread
            Thread t = new Thread(new LoadTestedClass(i, startingTimeMills) {
                public void runConcurently() {
                    logger.debug("Thread " + threadNum + " started");
                    long t0 = System.nanoTime();
                    
                    InitParam initParameters = initialize();
                    
                    long t1 = System.nanoTime();
                    for (int i = 0; i < subsequentCallsInThread; i++) {
                        
                        // call pre-method
                        PreCallParam object = preCall(threadNum, i, initParameters);
                                    
                        // sleep
                        if (maxSleepBetweenPreAndPostCall > 0) {
                            try {
                                Thread.sleep((long)(Math.random() * maxSleepBetweenPreAndPostCall));
                            } catch (InterruptedException e) {
                            }
                        }
                        
                        // call post-method
                        postCall(object);
                        
                    }
                    long t2 = System.nanoTime();
                    
                    logger.debug("Thread " + threadNum + " run initialized in " + (t1 - t0)/1000000);
                    logger.debug("Thread " + threadNum + " run pre processed in " + (t2 - t1)/1000000);
                    logger.debug("Thread " + threadNum + " run end");
                }
            });
            
            loadThreadsMap.put(new Integer(i), t);
        }
        
        logger.info("runLoadTest - running created threads");
        for (int i = 0; i < threadCount; i++) {
            // taking a thread definition to run it
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.start();
        }
        
        logger.info("runLoadTest - waiting for running threads to finish");
        for (int i = 0; i < threadCount; i++) {
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.join();
        }

        logger.info("runLoadTest - load test took: " + ((double)System.currentTimeMillis() - t0)/1000 + "sec");

        
        logger.info("runLoadTest - waiting after load test");
        try {
            Thread.sleep(sleepAfterTest);
        } catch (InterruptedException e) {
        }
                
        logger.info("runLoadTest - end");
    }
    
}