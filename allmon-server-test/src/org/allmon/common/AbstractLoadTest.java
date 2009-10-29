package org.allmon.common;

import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractLoadTest<OP, PCP> extends TestCase {

    private static final Log logger = LogFactory.getLog(AbstractLoadTest.class);
    
    public abstract OP initialize();
    
    public abstract PCP preCall(int thread, int iteration, OP initParameters);
    
    public abstract void postCall(PCP preCallParameters);
    
    //public abstract void ending(OP initParameters, PCP preCallParameters);
        
    public void runLoadTest(long threadCount, long startingTimeMills, 
            final long subsequentCallsInThreadSleepMax, final long subsequentCallsInThread, 
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
                    
                    OP initParameters = initialize();
                    
                    long t1 = System.nanoTime();
                    for (int i = 0; i < subsequentCallsInThread; i++) {
                        
                        PCP object = preCall(threadNum, i, initParameters);
                                                
                        if (subsequentCallsInThreadSleepMax > 0) {
                            try {
                                Thread.sleep((long)(Math.random() * subsequentCallsInThreadSleepMax));
                            } catch (InterruptedException e) {
                            }
                        }
                        
                        postCall(object);
                        
                    }
                    
                    long t2 = System.nanoTime();
                    
                    logger.debug("run initialized in " + (t1 - t0)/1000000);
                    logger.debug("run pre processed in " + (t2 - t1)/1000000);
                    //logger.debug("run post processed in " + (t3 - t2)/1000000);
                    
                    logger.debug("MetricMessage end");
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