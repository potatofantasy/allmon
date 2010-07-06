package org.allmon.client.agent.advices;

import org.allmon.client.agent.AgentContext;

abstract class AllmonAdvice {

	private AgentContext agentContext;
    
	private String name;
	
	public void setAgentContext(AgentContext agentContext) {
		this.agentContext = agentContext;
	}
	
	protected AgentContext getAgentContext() {
		return agentContext;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
