package org.allmon.server.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class LoadTestedClass implements Runnable {

    private static final Log logger = LogFactory.getLog(LoadTestedClass.class);
        
    protected int threadNum;
	protected long startingTime;
	
	public LoadTestedClass(int threadNum, long startingTime) {
		this.threadNum = threadNum;
		this.startingTime = startingTime;
	}
	
	public final void run() {
		logger.debug("run - start - " + threadNum);
		try {
			Thread.sleep(Math.round(startingTime * Math.random()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("run - running - " + threadNum);
        runConcurently();
		logger.debug("run - end - " + threadNum);
	}

	public abstract void runConcurently();
	
}
