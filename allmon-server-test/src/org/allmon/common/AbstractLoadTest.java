package org.allmon.common;

import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractLoadTest<InitParam, PreCallParam> extends TestCase {

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
        logger.debug("runLoadTest - start");
        
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        logger.debug("runLoadTest - creating definitions of threads");
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
        
        logger.debug("runLoadTest - running threads");
        for (int i = 0; i < threadCount; i++) {
            // taking a thread definition to run it
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.start();
        }
        
        logger.debug("runLoadTest - waiting for running threads");
        for (int i = 0; i < threadCount; i++) {
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.join();
        }
        
        logger.debug("runLoadTest - waiting after load test");
        try {
            Thread.sleep(sleepAfterTest);
        } catch (InterruptedException e) {
        }
                
        logger.debug("runLoadTest - end");
    }
    
}