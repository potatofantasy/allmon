package org.allmon.client.agent.aop;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class Advisor4Allmon {
	
	private AgentContext agentContext;
    
    public void setAgentContext(AgentContext agentContext) {
		this.agentContext = agentContext;
	}

	public AgentContext getAgentContext() {
		return agentContext;
	}

	public void logBeforeMethodCall() {
//        param: AgentContext agentContext
//        param: String classNameCalled, String methodNameCalled, String classNameCalling, String methodNameCalling
		
		System.out.println("Start AOP");
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                this.getClass().getName(), "method", "", "", -1); // TODO review duration time param
        
        JavaCallAgent agent = new JavaCallAgent(agentContext, metricMessage);
        agent.entryPoint();
    }

    public void logAfterMethodCall() {
//        param: JavaCallAgent agent
//        param: Exception exception
//        agent.exitPoint();
    	System.out.println("Stop AOP");
    	agentContext.stop();
    }
    
}
