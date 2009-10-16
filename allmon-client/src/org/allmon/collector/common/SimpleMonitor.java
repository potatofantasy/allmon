package org.allmon.collector.common;

import org.allmon.collector.core.BasicMonitor;
import org.allmon.collector.core.ResourceIdentifier;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;



public class SimpleMonitor extends BasicMonitor 
{
	private ResourceIdentifier resourceIdentifier;

	public void startMonitoring(ResourceIdentifier resourceIdentifier) 
	{
		setTimer(new SimpleTimer());
		basicTimer.startTimer();
		this.resourceIdentifier =  resourceIdentifier;
	}

	public void stopMonitoring() 
	{
		long timeTaken = basicTimer.stopTimer();
		
		storeMonitorData(timeTaken);
	}

	private void storeMonitorData(long timeTaken) 
	{
		String className = resourceIdentifier.getResourceName();
		String methodName = resourceIdentifier.getResourceName();
		String user = resourceIdentifier.getInvokerId();
		
		MetricMessage metricMessage = MetricMessageFactory.createClassMessage(className, methodName, user, timeTaken);
		
	}

}
