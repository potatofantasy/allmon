package org.allmon.client.agent.advices;

import org.allmon.client.agent.AgentContext;

abstract class AllmonAdvice {

	private AgentContext agentContext;
    
	private String name;
	
	private boolean silentMode = true;
	
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
	
	public boolean isSilentMode() {
		return silentMode;
	}
	
	public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}
	
}
