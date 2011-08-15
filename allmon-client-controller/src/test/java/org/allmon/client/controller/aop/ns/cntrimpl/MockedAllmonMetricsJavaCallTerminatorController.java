package org.allmon.client.controller.aop.ns.cntrimpl;

import org.allmon.client.controller.terminator.AbstractAllmonMetricsJavaCallTerminatorController;
import org.aspectj.lang.ProceedingJoinPoint;

public class MockedAllmonMetricsJavaCallTerminatorController extends AbstractAllmonMetricsJavaCallTerminatorController {

	@Override
	public boolean terminate(ProceedingJoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		
		System.out.println("allmonMetricsReceiver.metricsDataStore.size(): " + 
				allmonMetricsReceiver.metricsDataStore.size());
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
