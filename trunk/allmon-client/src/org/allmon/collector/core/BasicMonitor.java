package org.allmon.collector.core;




public abstract class BasicMonitor 
{	
	protected Timer basicTimer;
	
	protected void setTimer(Timer timer)
	{
		basicTimer = timer;
	}
	
	public abstract void startMonitoring(ResourceIdentifier resourceIdentifier);
	public abstract void stopMonitoring();
}
