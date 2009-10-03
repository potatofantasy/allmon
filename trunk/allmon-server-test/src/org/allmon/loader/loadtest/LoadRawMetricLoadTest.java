package org.allmon.loader.loadtest;

import java.util.HashMap;

import junit.framework.TestCase;

import org.allmon.server.loader.LoadRawMetric;
import org.allmon.server.loader.RawMetric;

public class LoadRawMetricLoadTest extends TestCase {

    private final static long THREADS_COUNT = 15;
    private final static long STARTING_TIME_MILLIS = 10;

    public void testMain() throws InterruptedException {
		System.out.println("m2 - start");
		
		HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
		System.out.println("m2 - creating definitions of threads");
		for (int i = 0; i < THREADS_COUNT; i++) {
			// creating a thread
			//Thread t = new Thread(new LoadRawMetricTest(i, STARTING_TIME_MILLIS));
		    Thread t = new Thread(new LoadTestedClass(i, STARTING_TIME_MILLIS) {
		        public void runConcurently() {
    	            System.out.println("LoadRawMetric started");
    	            long t0 = System.nanoTime();
    	            
    	            LoadRawMetric loadRawMetric = new LoadRawMetric(); //new LoadRawMetric(threadNum, startingTime);
    	            long t1 = System.nanoTime();
    	            
    	            for (int i = 0; i < 2000; i++) {
    	                RawMetric metric = new RawMetric();
    	                String str = ">" + Math.random() + ">" + Math.random() + ">" + Math.random() + ">" + Math.random();
    	                metric.setMetric(str);
    	                loadRawMetric.storeMetric(metric);
    	            }
    	            
    	            long t2 = System.nanoTime();
    	            
    	            System.out.println("LoadRawMetric initialized in " + (t1 - t0)/1000000);
    	            System.out.println("LoadRawMetric metrics loaded in " + (t2 - t1)/1000000);
    	                        
    	            System.out.println("LoadRawMetric end");
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
