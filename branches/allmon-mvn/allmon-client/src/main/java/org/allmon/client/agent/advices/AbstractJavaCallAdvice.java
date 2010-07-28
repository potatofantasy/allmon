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
		//System.out.println("AbstractJavaCallAdvice created");
		logger.debug("AbstractJavaCallAdvice created - name " + getName());
	}
	
	protected final Object profile(ProceedingJoinPoint call) throws Throwable {
		//System.out.println(" >>> before method call");
		if (!isSilentMode()) {
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
			//System.out.println(" >>> after method call");
			if (!isSilentMode()) {
				logger.debug(getName() + " >>> after method call");
			}
			if (agent != null) {
				agent.exitPoint(new Exception(t)); // TODO move to Throwable 
			}
		}
	}

	protected MetricMessage createMetricMessage(JoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		Object [] args = call.getArgs();
		//System.out.println("profile >>> " + className + "." + methodName);
		
		// FIXME add a parameter which switch this method on/off
		//Caller caller = getOriginalCaller(className, methodName);
		Caller caller = new Caller();
		
		MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                className, methodName, caller.className, caller.methodName, 0); // TODO review duration time param
		
		// FIXME add a parameter which switch storing calls parameters on/off
		metricMessage.setParameters(args);
		
		return metricMessage;
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
