package org.allmon.client.scheduler;

import junit.framework.TestCase;

import org.allmon.client.agent.ShellCallAgent;
import org.allmon.client.agent.URLCallAgent;

public class AgentCallerMainTest extends TestCase {

	public void testMain() throws Exception {
		String [] args = {ShellCallAgent.class.getName(), ""};
		//String [] args = {SimpleMetricMessageAgent.class.getName(), ""};
		AgentCallerMain.main(args);
	}

    public void testExecuteAgentTaskable() throws Exception {
        URLCallAgent agent = new URLCallAgent();
        String[] classParamsString = new String[]{"http://www.google.com", "\\d\\d\\d\\d"};        
        AgentCallerMain caller = new AgentCallerMain();
        caller.executeAgentTaskable(agent, classParamsString);
    }

	
} 	
