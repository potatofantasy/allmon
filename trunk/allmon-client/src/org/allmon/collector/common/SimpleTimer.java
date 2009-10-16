package org.allmon.collector.common;

import org.allmon.collector.core.Timer;


public class SimpleTimer implements Timer 
{
	private long startTime;
	
	public void startTimer() 
	{
		startTime = System.nanoTime();
	}

	public long stopTimer() 
	{
		return (System.nanoTime() - startTime);
	}
}
