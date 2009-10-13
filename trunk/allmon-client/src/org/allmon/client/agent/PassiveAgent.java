package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

/**
 * 
 *
 */
abstract class PassiveAgent {

	private MetricMessageSender metricMessageSender;
	
	PassiveAgent(MetricMessage metricMessage) {
		metricMessageSender = new SimpleMetricMessageSender(metricMessage);
	}
	
	protected MetricMessageSender getMetricMessageSender() {
		return metricMessageSender;
	}
	
}
