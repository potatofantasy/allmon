package org.allmon.client.agent;

import junit.framework.TestCase;

public class ShellCallAgentTest extends TestCase {

	public void testExecute() {
		ShellCallAgent agent = new ShellCallAgent();
		agent.setShellCommand("cmd") ;
		agent.setSearchPhrase("\\d");
		agent.execute();
	}
}
