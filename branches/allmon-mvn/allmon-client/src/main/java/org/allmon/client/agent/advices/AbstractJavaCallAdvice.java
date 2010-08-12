package org.allmon.client.agent.advices;

import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AbstractJavaCallAdvice extends AllmonAdvice {

	private final Log logger = LogFactory.getLog(AbstractJavaCallAdvice.class);

	public AbstractJavaCallAdvice() {
		logger.debug("AbstractJavaCallAdvice created - name " + getName());
	}
	
	protected final Object profile(ProceedingJoinPoint call) throws Throwable {
		if (isVerboseMode()) {
			logger.debug(getName() + " >>> before method call");
		}
		JavaCallAgent agent = null;
		try {
			MetricMessage metricMessage = createMetricMessage((JoinPoint)call);
			agent = new JavaCallAgent(agentContext, metricMessage);
	        agent.entryPoint();
    	} catch (Throwable t) {
    	}
    	
    	// execute an advised method
    	Throwable t = null;
		try {
			return call.proceed();
		} catch (Throwable th) {
			t = th;
			throw th;
		} finally {
			if (isVerboseMode()) {
				logger.debug(getName() + " >>> after method call");
			}
			if (agent != null) {
				agent.exitPoint(t);
			}
		}
	}

	protected MetricMessage createMetricMessage(JoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		if (isVerboseMode()) {
			logger.debug("profile >>> " + className + "." + methodName);
		}
		
		// FIXME add a parameter which switch this method on/off
		Caller caller = new Caller();
		if (isFindCaller()) {
			caller.getOriginalCaller(className, methodName);
		}
		
		MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                className, methodName, caller.className, caller.methodName);
		
		// acquiring call parameters
		if (isAcquireCallParameters()) {
			metricMessage.setParameters(call.getArgs());
		}
		
		return metricMessage;
	}
	
    protected class Caller {
    	
    	String className = "";
    	String methodName = "";
    
	    private void getOriginalCaller(String className, String methodName) {
			StackTraceElement[] elements = new Throwable().getStackTrace();
			for (int i = 1; i < elements.length; i++) {
				String iclassName = elements[i].getClassName();
				String imethodName = elements[i].getMethodName();
				//String ifileName = elements[i].getFileName();
				
				if (imethodName.equals(methodName) 
						&& iclassName.substring(0, iclassName.indexOf("$$")).equals(className)) {
					className = elements[i+1].getClassName();
					methodName = elements[i+1].getMethodName();
					
					if (isVerboseMode()) {
						logger.debug("profile >>> original caller >>> " + className + "." + methodName);
					}
					
					return;
				}
			}
	    }
    
    }
    
}
