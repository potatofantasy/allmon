package org.allmon.client.controller.terminator;

import org.allmon.client.controller.advices.Controller;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AbstractJavaCallTerminatorController implements Controller {
	
	public abstract boolean terminate(ProceedingJoinPoint call);
	
}
