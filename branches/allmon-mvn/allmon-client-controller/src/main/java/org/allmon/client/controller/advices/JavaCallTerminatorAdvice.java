package org.allmon.client.controller.advices;

import org.allmon.client.controller.terminator.AbstractJavaCallTerminatorController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * This advice can be use together with Spring AOP.
 * 
 * This class is used internally by allmon controller Spring namespace engine.
 * Can be easily applied to spring beans by following code:
 * 
 * <pre>{@code
 *  <allcon:controller>
		<allmon:javaCallTerminator id="id1" controllerRef="controllerBeanForTermination" 
		                      pointcutExpression="..."
			...
	</allcon:controller>
 * }</pre>
 * 
 */
public class JavaCallTerminatorAdvice extends AbstractJavaCallControllerAdvice {

	private final Log logger = LogFactory.getLog(JavaCallTerminatorAdvice.class);

	public JavaCallTerminatorAdvice() {
		logger.debug("JavaCallTerminatorAdvice created - name " + getName());
	}
	
	public boolean doConcreteEntryControl(ProceedingJoinPoint call) throws JavaCallTerminationException {
		AbstractJavaCallTerminatorController terminatorController =
			(AbstractJavaCallTerminatorController)getController();
		
		boolean terminate = terminatorController.terminate(call);
		
		if (terminate) {
			throw new JavaCallTerminationException();
		}
		
		return terminate;
	}
	
}
