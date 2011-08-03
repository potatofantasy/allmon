package org.allmon.client.controller.advices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

abstract class AbstractJavaCallControllerAdvice extends ControllerAdvice { //extends AllmonAdvice {

	private final Log logger = LogFactory.getLog(AbstractJavaCallControllerAdvice.class);

	public AbstractJavaCallControllerAdvice() {
		logger.debug("AbstractJavaCallControllerAdvice created - name " + getName());
	}
	
	protected final Object controll(ProceedingJoinPoint call) throws Throwable {
		if (isVerboseMode()) {
			logger.debug(getName() + " >>> before method call");
		}

		// XXX entry point
		
		// can be for terminating but also for micro-scheduling
		doEntryControl(call); 
    	
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

			// XXX exit point

		}
	}

	abstract boolean doConcreteEntryControl(Object o) throws Throwable;
	
	boolean doEntryControl(ProceedingJoinPoint call) throws Throwable {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		if (isVerboseMode()) {
			logger.debug("profile >>> " + className + "." + methodName);
		}
		
		getController().setCallData(call);
		
		// TODO get data from Aggregator (for controller use queue), raw metrics and SLA calculated values
		
		boolean didControll = doConcreteEntryControl(className);
		
		return didControll;
	}
	
}
