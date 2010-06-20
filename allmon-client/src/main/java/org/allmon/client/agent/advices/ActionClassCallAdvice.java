package org.allmon.client.agent.advices;

import javax.servlet.http.HttpServletRequest;

import org.allmon.client.agent.HttpClientCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.aspectj.lang.ProceedingJoinPoint;

public class ActionClassCallAdvice extends AllmonAdvice {

	private HttpClientCallAgent agent;
	
	public Object profile(ProceedingJoinPoint call) throws Throwable {
		System.out.println(">>> before method call");
		try {
			Object [] args = call.getArgs();
			String className = call.getSignature().getDeclaringTypeName();
			String methodName = call.getSignature().getName();
			
			// FIXME finish this implementation
			// TODO get request parameters
			HttpServletRequest request = (HttpServletRequest)args[3]; // TODO add casting exception management 
			String user = ""; // TODO get user id object - using name of request param name set from instrumentation configuration
			String webSessionId = request.getRequestedSessionId();
			
    		MetricMessage metricMessage = MetricMessageFactory.createActionClassMessage(
    				className, user, webSessionId, request);
    		//metricMessage.setParameters(args); // TODO review other parameters setting
    		agent = new HttpClientCallAgent(getAgentContext(), metricMessage);
	        agent.requestReceived();
    	} catch (Throwable t) {
    	}
    	    	
    	// execute an advised method
    	boolean finishedWithException = false;
		try {
			return call.proceed();
		} catch (Exception ex) {
			if (agent != null) {
				//agent.requestSent(ex); // FIXME add this API
			}
			finishedWithException = true;
			return null; // TODO review this line
		} finally {
			System.out.println(">>> after method call");
			if (agent != null && !finishedWithException) {
				agent.requestSent();
			}
		}
	}

}
