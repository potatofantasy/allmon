package org.allmon.client.agent;

import org.allmon.client.agent.ActiveAgent;
import org.allmon.client.agent.AgentContext;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agents sends heart-beat (signals) ping messages only. 
 * Server is notified that active agents scheduler and allmon client-side 
 * infrastructure works fine.
 * 
 */
public class HeartbeatAgent extends ActiveAgent  {

	private static final Log logger = LogFactory.getLog(HeartbeatAgent.class);

	public HeartbeatAgent(AgentContext agentContext) {
		super(agentContext);
	}
    
    MetricMessageWrapper collectMetrics() {
        MetricMessage metricMessage = MetricMessageFactory.createPingMessage(getAgentSchedulerName());
        return new MetricMessageWrapper(metricMessage);
    }
    
}
