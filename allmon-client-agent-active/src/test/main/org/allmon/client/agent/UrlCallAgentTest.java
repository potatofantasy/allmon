package org.allmon.client.agent;


import junit.framework.TestCase;

public class UrlCallAgentTest extends TestCase {

    public void testExecute() {
        AgentContext agentContext = new AgentContext();
        UrlCallAgent agent = new UrlCallAgent(agentContext);
//        agent.setParameters(new String[]{
//                //"http://www.google.com", 
//                "http://www.google.com/#hl=en&q=qwerty",
//                "\\d\\d\\d\\d"
//                }); // FIXME clean code
        agent.execute();
        agentContext.stop();
    }
    
}
