package org.allmon.client.agent.http;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.HttpUrlCallAgent;
import org.allmon.client.agent.HttpUrlCallAgentBooleanStrategy;

public class HttpHealthCheck {

    public static void main(String[] args) throws InterruptedException {
    	AgentContext agentContext = new AgentContext();
    	try {
		    HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
	        agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
//	        agent.setParameters(new String[]{
//	                //"http://www.google.com/#hl=en&q=qwerty",
//	                "http://lontd01/AdminConsole/statuscheck/default.aspx/CheckStatus", 
//	                "\"Success\":true",
//	                "application/json; charset=utf-8", //"text/html", //"application/x-www-form-urlencoded",
//	                "{ 'componentChecker': 'TTC.iTropics.ComponentCheckers.ITropicsServiceComponentChecker, TTC.iTropics.ComponentCheckers' }"
//	            }); // FIXME change way of setting params
	        agent.execute();
	        
	        Thread.sleep(2000);
    	} finally {
	        // kill buffering thread and close connections to broker
	        agentContext.stop();
    	}
        System.out.println("end.");
    }
    
}
