package org.allmon.client.controller.advices;

import org.aspectj.lang.ProceedingJoinPoint;

public interface Controller {

	public void setCallData(ProceedingJoinPoint call);
	
}
