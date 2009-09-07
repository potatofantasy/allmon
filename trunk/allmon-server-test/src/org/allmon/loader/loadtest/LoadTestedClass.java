package org.allmon.loader.loadtest;

public abstract class LoadTestedClass implements Runnable {

    protected int threadNum;
	protected long startingTime;
	
	public LoadTestedClass(int threadNum, long startingTime) {
		this.threadNum = threadNum;
		this.startingTime = startingTime;
	}
	
	public final void run() {
		System.out.println("run - start - " + threadNum);
		try {
			Thread.sleep(Math.round(startingTime * Math.random()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("run - running - " + threadNum);
        runConcurently();
		System.out.println("run - end - " + threadNum);
	}

	public abstract void runConcurently();
	
}
