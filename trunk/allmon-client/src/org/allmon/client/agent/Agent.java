/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;

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
    
    private final static AgentMetricBuffer metricBuffer = new AgentMetricBuffer();
    
	Agent() {
	}
	
    void addMetricMessage(MetricMessage metricMessage) {
        metricBuffer.add(metricMessage);
    }
	
}
