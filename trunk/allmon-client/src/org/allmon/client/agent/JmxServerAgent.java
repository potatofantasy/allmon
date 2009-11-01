package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;

public class JmxServerAgent extends ActiveAgent {

	public JmxServerAgent(AgentContext agentContext) {
		super(agentContext);
	}

	public MetricMessageWrapper collectMetrics() {
        return null;
	}

    void decodeAgentTaskableParams() {
    }

}
