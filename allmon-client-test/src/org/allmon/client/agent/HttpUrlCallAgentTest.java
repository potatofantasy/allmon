package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

    public void testExecute() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setParameters(new String[]{
                    "http://www.google.com",
                    "html",
                    "text/html", 
                    "0",
                    "www.google.com", 
                    "www.google.com-Checker"});
            agent.execute();
        
            Thread.sleep(5000);
        } finally {
            agentContext.stop();
        }
    }
    
}