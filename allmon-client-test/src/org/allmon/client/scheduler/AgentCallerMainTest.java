package org.allmon.client.scheduler;

import junit.framework.TestCase;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.SonobuoyAgent;
import org.allmon.client.agent.UrlCallAgent;

public class AgentCallerMainTest extends TestCase {

	public void testMain() throws Exception {
		//String [] args = {ShellCallAgent.class.getName(), ""};
		//String [] args = {SimpleMetricMessageAgent.class.getName(), ""};
	    String [] args = {SonobuoyAgent.class.getName(), ""};
        AgentCallerMain.main(args);
	}

    public void testExecuteAgentTaskable() throws Exception {
        AgentContext agentContext = new AgentContext();
        UrlCallAgent agent = new UrlCallAgent(agentContext);
        String[] classParamsString = new String[]{"http://www.google.com", "\\d\\d\\d\\d"};        
        AgentCallerMain caller = new AgentCallerMain();
        caller.executeAgentTaskable(agent, classParamsString);
        agentContext.stop();
    }
	
} 	
