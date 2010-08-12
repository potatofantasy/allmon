package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class JavaCallAgentTest extends TestCase {

    private final AgentContext agentContext = new AgentContext();
    
    public static void main(String[] args) throws Exception {
        JavaCallAgentTest test = new JavaCallAgentTest();
        test.testSimple();
    }
    
    public void testSimple() throws Exception {
        MetricMessage message = MetricMessageFactory.createActionClassMessage(
                this.getClass().getName(), "user", "webSessionId", null);
        
        JavaCallAgent agent = new JavaCallAgent(agentContext, message);
        agent.entryPoint();
        Thread.sleep(1500);
        
        message.setParameters(new Object[]{"str1", new String[]{"str2a", "str2b"}});
        message.setThrowable(new Exception("Something happend so an exception is thrown"));
        
        agent.exitPoint();
        
        Thread.sleep(2000);
        agentContext.stop();
    }

}