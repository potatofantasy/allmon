package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

    public void testCallWikipedia() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setParameters(new String[]{
                    "org.allmon.client.agent.HttpUrlCallAgentBooleanStrategy",
                    "http://www.wikipedia.org",
                    "wikipedia",
                    "text/html",
                    "-",
                    "www.wikipedia.com",
                    "www.wikipedia.com-Checker", 
                    "true"});
            agent.execute();
            Thread.sleep(2000);
        } finally {
            agentContext.stop();
        }
    }
    
    public void testCallGoogle() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setParameters(new String[]{
                    "org.allmon.client.agent.HttpUrlCallAgentBooleanStrategy",
                    "http://www.google.com",
                    "html",
                    "text/html",
                    "-",
                    "www.google.com",
                    "www.google.com-Checker",
                    "true"});
            agent.execute();
            Thread.sleep(2000);
        } finally {
            agentContext.stop();
        }
    }
    
}