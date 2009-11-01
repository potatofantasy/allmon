package org.allmon.client.agent;

import junit.framework.TestCase;

public class ShellCallAgentTest extends TestCase {

	public void testExecute() {
		ShellCallAgent agent = new ShellCallAgent(AgentContext.getInstance());
		agent.setShellCommand("cmd") ;
		agent.setSearchPhrase("\\d");
		agent.execute();
		AgentContext.getInstance().stop();
	}
}
