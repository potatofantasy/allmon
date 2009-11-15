package org.allmon.client.agent;

import junit.framework.TestCase;

public class JmxServerAgentTest extends TestCase {

    public void testSending() {
        AgentContext agentContext = new AgentContext();
        try {
            JmxServerAgent agent = new JmxServerAgent(agentContext);
            agent.setParameters(new String[]{
                    "", //"AgentAggregatorMain",
                    ""
                    });
            agent.execute();
        } finally {
            agentContext.stop();
        }
    }
    
}
