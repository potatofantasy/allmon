/**
 * 
 */
package org.allmon.client.agent;

/**
 * 
 *
 */
abstract class Agent {

	private MetricMessageSender messageSender;
	
	public Agent() {
		//TODO find out how to create/pass metric message sender
		//messageSender = new SimpleMetricMessageSender(message)
	}
	
	protected MetricMessageSender getMessageSender() {
		return messageSender;
	}
	
}
