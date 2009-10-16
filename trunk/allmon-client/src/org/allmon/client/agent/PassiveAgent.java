package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * This class let you monitor actual (real) interaction with a system. 
 * Metrics collected using this approach can be used to determine the actual 
 * service-level quality delivered to end-users and to detect errors 
 * or potential performance in the system. <br>
 * - Concrete agents extending this class can be very helpful in troubleshooting 
 * performance problems once they have occurred.<br>
 * - The most important drawback of this method is that something in the system 
 * has to be performed (triggered externally) to take and collect any metrics. 
 * We will have no defined service level if no action is called. 
 * 
 */
abstract class PassiveAgent extends Agent {

	private MetricMessageSender metricMessageSender;
	
	PassiveAgent(MetricMessage metricMessage) {
		metricMessageSender = new SimpleMetricMessageSender(metricMessage);
	}
	
	protected MetricMessageSender getMetricMessageSender() {
		return metricMessageSender;
	}
	
}
