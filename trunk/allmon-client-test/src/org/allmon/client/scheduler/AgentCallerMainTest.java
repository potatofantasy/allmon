package org.allmon.client.scheduler;

import junit.framework.TestCase;

public class AgentCallerMainTest extends TestCase {

	public void testMain() throws Exception {
		//String [] args = {ShellCallAgent.class.getName(), ""};
		//String [] args = {SimpleMetricMessageAgent.class.getName(), ""};
	    //String [] args = {SonobuoyAgent.class.getName(), ""};
	    String [] args = {
    	    org.allmon.client.agent.HttpUrlCallAgent.class.getName(), 
    	    "http://lontd01/AdminConsole/statuscheck/default.aspx/CheckStatus", 
    	    "\"Success\":true", 
    	    "application/json;charset=utf-8",
    	    "{'componentChecker':'TTC.iTropics.ComponentCheckers.ITropicsServiceComponentChecker,TTC.iTropics.ComponentCheckers'}"};
	        
	    AgentCallerMain.main(args);
	}

//    public void testExecuteAgentTaskable() throws Exception {
//        AgentContext agentContext = new AgentContext();
//        UrlCallAgent agent = new UrlCallAgent(agentContext);
//        String[] classParamsString = new String[]{"http://www.google.com", "\\d\\d\\d\\d"};        
//        AgentCallerMain caller = new AgentCallerMain();
//        caller.executeAgentTaskable(agent, classParamsString);
//        agentContext.stop();
//    }
	
	public static void main(String[] args) {
        String [] args2 = {
                org.allmon.client.agent.HttpUrlCallAgent.class.getName(), 
                "http://lontd01/AdminConsole/statuscheck/default.aspx/CheckStatus", 
                "\"Success\":true", 
                "application/json;charset=utf-8",
                "{'componentChecker':'TTC.iTropics.ComponentCheckers.ITropicsServiceComponentChecker,TTC.iTropics.ComponentCheckers'}"};
                
        AgentCallerMain.main(args2);
    }
	
} 	
