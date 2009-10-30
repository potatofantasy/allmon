/**
 * 
 */
package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;

/**
 * Top abstract level of agents definition.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has AgentMetricBuffer 
 * class instantiated for the whole live time.</b>
 * 
 */
abstract class Agent {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    // TODO review static field, if necessary maybe make a proper singleton/multiton implementation 
    private final static AgentMetricBuffer metricBuffer = AgentMetricBuffer.getInstance();
    
	Agent() {
	}
	
    void addMetricMessage(MetricMessage metricMessage) {
        metricBuffer.add(metricMessage);
    }
    
    public final static AgentMetricBuffer getMetricBuffer() {
        return metricBuffer;
    }
	
 }
