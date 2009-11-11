package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTSCTest extends TestCase {

    public static void main(String[] args) throws InterruptedException {
        HttpUrlCallAgentTSCTest agentTSCTest = new HttpUrlCallAgentTSCTest();
        agentTSCTest.testCallTSC();
    }
    
    public void testCallTSC() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setParameters(new String[]{
                    "org.allmon.client.agent.HttpUrlCallAgentTSCStrategy",
                    "http://lontd07:7777/tropics/tropicsui/util/screens/comn/TropicsServiceCheck.jsp?mode=dbcomplex",
                    "-",
                    "text/html",
                    "-",
                    "lontd07:7777/../TropicsServiceCheck.jsp",
                    "TropicsServiceCheck",
                    "false",
                    "GET"});
            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
    
}
