package org.allmon.client.agent;

import junit.framework.TestCase;

public class ShellCallAgentTest extends TestCase {

	public void testExecute() {
	    AgentContext agentContext = new AgentContext();
		ShellCallAgent agent = new ShellCallAgent(agentContext);
		//agent.setShellCommand("tasklist /V /FO csv /NH | find /c \"allmon\""); //cmd.exe
		agent.setShellCommand("dir | find /c \".\""); //cmd.exe
		agent.setSearchPhrase("[0-9]+");
		agent.execute();
		agentContext.stop();
	}
}
