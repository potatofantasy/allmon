package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

    public void testExecute() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setParameters(new String[]{
                    //"http://www.google.com/#hl=en&q=qwerty",
                    "http://lontd01/AdminConsole/statuscheck/default.aspx/CheckStatus", 
                    "\"Success\":true",
                    "application/json; charset=utf-8", //"text/html", //"application/x-www-form-urlencoded",
                    "{ 'componentChecker': 'TTC.iTropics.ComponentCheckers.ITropicsServiceComponentChecker, TTC.iTropics.ComponentCheckers' }"
                });
            agent.execute();
        
            Thread.sleep(5000);
        } finally {
            agentContext.stop();
        }
    }
    
}