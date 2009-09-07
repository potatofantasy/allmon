package org.allmon.loader.loadtest;

import java.util.HashMap;


public class LoadTestMain {

    private final static long THREADS_COUNT = 15;
    private final static long STARTING_TIME_MILLIS = 10;

    
	public static void main(String[] args) throws InterruptedException {
		//m1();
		m2();
	}

	static void m1() {
		System.out.println("m1 - start");
		for (int i = 0; i < THREADS_COUNT; i++) {
			Thread t = new Thread(new LoadRawMetricTest(i, STARTING_TIME_MILLIS));
			t.start();
		}
		System.out.println("m1 - end");
	}
	
	static void m2() throws InterruptedException {
		System.out.println("m2 - start");
		
		HashMap loadThreadsMap = new HashMap();
		
		System.out.println("m2 - creating definitions of threads");
		for (int i = 0; i < THREADS_COUNT; i++) {
			// creating a thread
			Thread t = new Thread(new LoadRawMetricTest(i, STARTING_TIME_MILLIS));
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
