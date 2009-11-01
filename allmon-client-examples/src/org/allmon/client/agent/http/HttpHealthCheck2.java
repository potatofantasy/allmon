package org.allmon.client.agent.http;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.HttpUrlCallAgent;
import org.allmon.client.agent.HttpUrlCallAgentBooleanStrategy;

public class HttpHealthCheck2 {

	public static void main(String[] args) throws InterruptedException {
		AgentContext agentContext = AgentContext.getInstance();
    	try {
		    HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
		    agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
	        agent.setParameters(new String[]{
	                "http://localhost:8161/", //"http://www.google.com/#hl=en&q=qwerty",
	                "\\d\\d",
	                "text/html", //"application/x-www-form-urlencoded",
	                ""
	            });
	        try {
	            agent.execute();
	        } catch (Exception e) {
	        }
	        
	        Thread.sleep(2000);
    	} finally {
	        // kill buffering thread and close connections to broker
	        agentContext.stop();
    	}
        System.out.println("end.");

	}

}
