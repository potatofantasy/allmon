package org.allmon.client.agent;

import java.util.HashMap;

import junit.framework.TestCase;

import org.allmon.common.MetricMessage;
import org.allmon.loader.loadtest.LoadTestedClass;

public class SimpleMetricMessageSenderLoadTest extends TestCase {

    private final static long THREADS_COUNT = 5;
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 100;
    
    public void testMain() throws InterruptedException {
        System.out.println("m2 - start");
        
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        System.out.println("m2 - creating definitions of threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // creating a thread
            Thread t = new Thread(new LoadTestedClass(i, STARTING_TIME_MILLIS) {
                public void runConcurently() {
                    System.out.println("MetricMessage started");
                    long t0 = System.nanoTime();
                    
                    long t1 = System.nanoTime();
                    
                    for (int i = 0; i < SUBSEQUENT_CALLS_IN_THREAD; i++) {
                        MetricMessage metricMessage = 
                            MetricMessageFactory.createClassMessage("className"+i, "methodName", "user"+i, 1);
                        SimpleMetricMessageSender sender = new SimpleMetricMessageSender(metricMessage);
                        sender.insertEntryPoint();
                        try {
                            Thread.sleep((long)(Math.random() * SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX));
                        } catch (InterruptedException e) {
                        }
                        sender.insertExitPoint();
                    }
                    
                    long t2 = System.nanoTime();
                    
                    System.out.println("MetricMessage initialized in " + (t1 - t0)/1000000);
                    System.out.println("MetricMessage metrics sent in " + (t2 - t1)/1000000);
                                
                    System.out.println("MetricMessage end");
                }
            });
            
            loadThreadsMap.put(new Integer(i), t);
        }
        
        System.out.println("m2 - running threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // taking a thread definition to run it
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.start();
        }
        
        System.out.println("m2 - waiting for running threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.join();
        }
        
        System.out.println("m2 - end");
    }

}
