package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * This class can be used to monitor interactions inside a monitored 
 * java application.
 * 
 */
public class JavaCallAgent extends PassiveAgent {
	
	public JavaCallAgent(MetricMessage metricMessage) {
		super(metricMessage);
	}

	public void entryPoint() {
		getMetricMessageSender().sendEntryPoint();
	}
	
	public void exitPoint() {
		getMetricMessageSender().sendExitPoint(null);
	}
	
	public void exitPoint(Exception exception) {
		getMetricMessageSender().sendExitPoint(exception);
	}
	
}
