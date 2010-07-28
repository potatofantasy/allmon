package org.allmon.client.agent.advices;

import javax.servlet.http.HttpServletRequest;

import org.allmon.client.agent.HttpClientCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * TODO review and decide if not redundant with other advises/interceptors
 * 
 * @prototype 
 */
public class HttpClientCallAdvice extends AllmonAdvice {

	private HttpClientCallAgent agent;
	
	public Object profile(ProceedingJoinPoint call) throws Throwable {
		System.out.println(">>> before method call");
		try {
			Object [] args = call.getArgs();
			call.getKind();
			String className = call.getSignature().getDeclaringTypeName();
			String methodName = call.getSignature().getName();
			call.getSourceLocation().getWithinType();
			
			// FIXME finish this implementation
			// TODO get request parameters
			HttpServletRequest request = (HttpServletRequest)args[3]; // TODO add casting exception management 
			String user = ""; // TODO get user id object - using name of request param name set from instrumentation configuration
			String webSessionId = request.getRequestedSessionId();
			
			MetricMessage metricMessage = MetricMessageFactory.createActionClassMessage(
    				className, user, webSessionId, request);
	        
			metricMessage.setParameters(args);
    		agent = new HttpClientCallAgent(agentContext, metricMessage);
	        agent.dataReceivedByClient();
    	} catch (Throwable t) {
    	}
    	
    	// execute an advised method
		try {
			return call.proceed();
		} finally {
			System.out.println(">>> after method call");
			if (agent != null) {
				// FIXME finish this implementation
				//agent.exitPoint();
			}
		}
	}
	
}
