package org.allmon.client.scheduler;

import org.allmon.client.agent.ShellCallAgent;
import org.allmon.client.agent.SimpleMetricMessageAgent;

import junit.framework.TestCase;

public class AgentCallerMainTest extends TestCase {

	public void testMain() throws Exception {
		String [] args = {ShellCallAgent.class.getName(), ""};
		//String [] args = {SimpleMetricMessageAgent.class.getName(), ""};
		AgentCallerMain.main(args);
	}
		
} 	
