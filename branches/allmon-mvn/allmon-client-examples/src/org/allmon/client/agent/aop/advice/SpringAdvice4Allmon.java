package org.allmon.client.agent.aop.advice;

import org.allmon.client.agent.AgentContext;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class SpringAdvice4Allmon {
	
	private static AgentContext agentContext;
    
    public void setAgentContext(AgentContext agentContext) {
		this.agentContext = agentContext;
	}

	public AgentContext getAgentContext() {
		return agentContext;
	}
	
	private long startTime;

	public void logBeforeMethodCall() {
//        param: AgentContext agentContext
//        param: String classNameCalled, String methodNameCalled, String classNameCalling, String methodNameCalling
		
		
		startTime = System.nanoTime();
		/*System.out.println("Start AOP");
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                this.getClass().getName(), "method", "", "", -1); // TODO review duration time param
        
        JavaCallAgent agent = new JavaCallAgent(agentContext, metricMessage);
        agent.entryPoint();*/
    }

    public void logAfterMethodCall() {
//        param: JavaCallAgent agent
//        param: Exception exception
//        agent.exitPoint();
    	
    	long duration = System.nanoTime() - startTime;
    	
    	MetricMessage metricMessage = MetricMessageFactory.createClassMessage(this.getClass().getName(), "method", "", "", duration); 
    	/*System.out.println("Stop AOP");
    	agentContext.stop();*/
    }
    
}
