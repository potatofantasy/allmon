package org.allmon.client.agent.advices;

import org.allmon.client.agent.AgentContext;

abstract class AllmonAdvice {

	private AgentContext agentContext;
    
	public void setAgentContext(AgentContext agentContext) {
		this.agentContext = agentContext;
	}
	
	protected AgentContext getAgentContext() {
		return agentContext;
	}
	
}
