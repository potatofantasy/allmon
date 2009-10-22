/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;

/**
 * Top abstract level of agents definition.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has JmsBrokerSampler 
 * class instantiated for the whole live time.</b>
 * 
 */
abstract class Agent {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    AgentsMetricMessageSender messageSender;
	
	Agent() {
		//TODO find out how to create/pass metric message sender
//		messageSender = new SimpleMetricMessageSender(message)
	}
	
//	protected MetricMessageSender getMessageSender() {
//		return messageSender;
//	}
	
}
