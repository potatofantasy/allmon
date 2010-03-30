package org.allmon.client.agent;

import junit.framework.TestCase;

public class SelfHealthCheckAgentTest extends TestCase {

    public void testCall() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            SelfHealthCheckAgent agent = new SelfHealthCheckAgent(agentContext);
            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
    
}
