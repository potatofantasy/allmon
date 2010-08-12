package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;

public class PassiveAgentMetricMessageSenderTest extends TestCase {

    public void testInsertEntryAndExitPoint() throws Exception {
        MetricMessage metricMessage = MetricMessageFactory4Test.createClassMessage("className", "methodName", "classNameX", "methodNameX");
        
        AgentContext agentContext = new AgentContext();
        PassiveAgent agent = new PassiveAgent(agentContext, metricMessage) {
        	
        };
        PassiveAgentMetricMessageSender sender = new PassiveAgentMetricMessageSender(agent); // TODO finish
        sender.insertEntryPoint();
        Thread.sleep(1234);
        sender.insertNextPoint();
    }
    
}
