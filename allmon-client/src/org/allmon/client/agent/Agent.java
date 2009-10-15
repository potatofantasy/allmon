/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;

/**
 * 
 *
 */
abstract class Agent {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
//	private MetricMessageSender messageSender;
	
	public Agent() {
		//TODO find out how to create/pass metric message sender
//		messageSender = new SimpleMetricMessageSender(message)
	}
	
//	protected MetricMessageSender getMessageSender() {
//		return messageSender;
//	}
	
}
