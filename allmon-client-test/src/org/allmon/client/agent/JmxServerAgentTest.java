package org.allmon.client.agent;

import junit.framework.TestCase;

public class JmxServerAgentTest extends TestCase {

    public void testSending() {
        AgentContext agentContext = new AgentContext();
        try {
            JmxServerAgent agent = new JmxServerAgent(agentContext);
            agent.setParameters(new String[]{
                    ".*activemq.*", //".*AgentAggregatorMain.*"
                    ".*java.lang:type=Memory.*", // all memory metrics
                    //"(.*java.lang:type=Memory.*used.*)|(.*java.lang:type=GarbageCollector.*)"
                    //"java.lang:"
                    //".*java.lang:type=Threading:CurrentThreadCpuTime" //"java.lang:type=Threading"
                    //".*java.lang:type=Runtime.*"
                    //java.lang:type=Compilation:
            });
            agent.execute();
        } finally {
            agentContext.stop();
        }
    }
    
}
