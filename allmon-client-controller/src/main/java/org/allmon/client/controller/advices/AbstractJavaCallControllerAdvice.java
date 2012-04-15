package org.allmon.client.controller.advices;

import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
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
		}
	}

	abstract boolean doConcreteEntryControl(ProceedingJoinPoint call) throws Throwable;
	
	boolean doEntryControl(ProceedingJoinPoint call) throws Throwable {
		String controlledClassName = call.getSignature().getDeclaringTypeName();
		String controlledMethodName = call.getSignature().getName();
		if (isVerboseMode()) {
			logger.debug("doEntryControl >>> " + controlledClassName + "." + controlledMethodName);
		}
		//System.out.println("doEntryControl >>> " + controlledClassName + "." + controlledMethodName);
		
		// this metric message object can be used for both termination and micro-scheduling, where:
		// metric value is time of control execution, and potential termination is marked as a control exception
		final MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
				controllerClassName(), controllerMethodName(), controlledClassName, controlledMethodName);
		// acquiring controlled call parameters + control action parameters
		//if (isAcquireCallParameters()) {
		//	metricMessage.setParameters(call.getArgs());
		//}
		JavaCallAgent agent = new JavaCallAgent(agentContext, metricMessage);
		
		boolean didControl = false;
		ControllerException controlExceptionThrown = null;
		try {
			// potential control action is performed
			didControl = doConcreteEntryControl(call);
		} catch (ControllerException ce) {
			didControl = true; // it was a control exception - the call execution has been terminated by controller
			controlExceptionThrown = ce;
			throw ce; // re-throw control exception from controller
		} catch (Throwable e) {
			didControl = false; // an error occurred - no deliberate control action
			throw e; // re-throw other exception from controller
		} finally {
			//System.out.println("doEntryControl >>> " + metricMessage + " >>> " + controlExceptionThrown);
			if (didControl) {
				if (controlExceptionThrown != null) {
					agent.exitPoint(controlExceptionThrown);
				} else {
					agent.exitPoint(new ControllerException("A controll action was applied"));
				}
			} else {
				agent.exitPoint();
			}
		}
		
		return didControl;
	}
	
	abstract String controllerClassName();
	abstract String controllerMethodName();
	
}
