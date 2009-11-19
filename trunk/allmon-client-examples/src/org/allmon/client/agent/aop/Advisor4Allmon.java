package org.allmon.client.agent.aop;

import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class Advisor4Allmon {
    
    public void logBeforeMethodCall() {
//        param: AgentContext agentContext
//        param: String classNameCalled, String methodNameCalled, String classNameCalling, String methodNameCalling
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                this.getClass().getName(), "method", "", "", -1); // TODO review duration time param
        
        JavaCallAgent agent = new JavaCallAgent(SpringHelloWorldController.agentContext, metricMessage);
        agent.entryPoint();
    }

    public void logAfterMethodCall() {
//        param: JavaCallAgent agent
//        param: Exception exception
//        agent.exitPoint();
    }
    
}
