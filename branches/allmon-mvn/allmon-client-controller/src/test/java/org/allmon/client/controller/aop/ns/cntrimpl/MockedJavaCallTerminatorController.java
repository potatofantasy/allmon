package org.allmon.client.controller.aop.ns.cntrimpl;

import org.allmon.client.controller.advices.AbstractJavaCallTerminatorController;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * This is a mock implementation of a controller for terminating java calls.
 * 
 * It has been defined in different package to instrumented spring beans (HelloWorldImpl) 
 * to avoid advising over controller methods and effectively facing StackOverFlow.
 * 
 */
public class MockedJavaCallTerminatorController extends AbstractJavaCallTerminatorController {

	@Override
	public boolean terminate() {
		ProceedingJoinPoint call = getCallData();
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		
//		if (methodName.length() > 6) {
//			return true;
//		} 
//		return false;
		
		System.out.println(call.getArgs().length);
		
		if (call.getArgs().length > 1) {
			return true;
		} 
		return false;
	}

}
