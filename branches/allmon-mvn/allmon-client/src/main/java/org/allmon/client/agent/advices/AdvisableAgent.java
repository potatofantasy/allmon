package org.allmon.client.agent.advices;

public interface AdvisableAgent {

	public void entryPoint();
	
	public void exitPoint(Throwable t);
	
}
