package org.allmon.client.agent;

import junit.framework.TestCase;

public class ShellCallAgentTest extends TestCase {

	public void testExecute() {
	    AgentContext agentContext = new AgentContext();
		ShellCallAgent agent = new ShellCallAgent(agentContext);
		agent.setShellCommand("cmd") ;
		agent.setSearchPhrase("\\d");
		agent.execute();
		agentContext.stop();
	}
}
