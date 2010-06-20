package org.allmon.client.agent.advices;

import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.aspectj.lang.ProceedingJoinPoint;

public class JavaCallAdvice extends AllmonAdvice {
	
	private JavaCallAgent agent;
	
	public Object profile(ProceedingJoinPoint call) throws Throwable {
		System.out.println(">>> before method call");
		try {
			Object [] args = call.getArgs();
			call.getKind();
			String className = call.getSignature().getDeclaringTypeName();
			String methodName = call.getSignature().getName();
			call.getSourceLocation().getWithinType();
			
			Caller caller = getOriginalCaller(className, methodName);
			
    		MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
	                className, methodName, caller.className, caller.methodName, 0); // TODO review duration time param
    		metricMessage.setParameters(args);
    		agent = new JavaCallAgent(getAgentContext(), metricMessage);
	        agent.entryPoint();
    	} catch (Throwable t) {
    	}
    	    	
    	// execute an advised method
    	boolean finishedWithException = false;
		try {
			return call.proceed();
		} catch (Exception ex) {
			if (agent != null) {
				agent.exitPoint(ex);
			}
			finishedWithException = true;
			return null; // TODO review this line
		} finally {
			System.out.println(">>> after method call");
			if (agent != null && !finishedWithException) {
				agent.exitPoint();
			}
		}
	}

    class Caller {
    	String className = "";
    	String methodName = "";
    }
    
    private Caller getOriginalCaller(String className, String methodName) {
		Caller caller = new Caller(); // String [] caller = {"", ""}; //{"callerClass", "callerMethod"};
    	
    	StackTraceElement[] elements = new Throwable().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String iclassName = elements[i].getClassName();
			String imethodName = elements[i].getMethodName();
			//String ifileName = elements[i].getFileName();
			
			if (imethodName.equals(methodName) 
				&& iclassName.substring(0, iclassName.indexOf("$$")).equals(className)) {
				caller.className = elements[i+1].getClassName();
				caller.methodName = elements[i+1].getMethodName();
				return caller;
			}
		}
		
    	return caller;
    }
    
}
