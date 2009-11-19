package org.allmon.client.agent.aop;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class Advisor4Allmon {

    private final AgentContext agentContext = new AgentContext(); // flushing problem!!!
    
    public void logBeforeMethodCall() {
//        String classNameCalled, String methodNameCalled, String classNameCalling, String methodNameCalling
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                this.getClass().getName(), "method", "", "", -1); // TODO review duration time param
        
        JavaCallAgent agent = new JavaCallAgent(agentContext, metricMessage);
        agent.entryPoint();
    }

    public void logAfterMethodCall() {
//        JavaCallAgent agent
//        agent.exitPoint();
    }
}
