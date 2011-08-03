package org.allmon.client.controller.advices;

import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AbstractJavaCallTerminatorController implements Controller {

	private ProceedingJoinPoint call;
	
	public final void setCallData(ProceedingJoinPoint call) {
		this.call = call;
	}
	
	// TODO hide this method - expose specific details
	public ProceedingJoinPoint getCallData() {
		return call;
	}
	
	public abstract boolean terminate();
	
}
