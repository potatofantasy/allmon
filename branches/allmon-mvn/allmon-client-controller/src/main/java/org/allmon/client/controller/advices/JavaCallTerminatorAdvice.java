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
	
	@Override
	public boolean doConcreteEntryControl(ProceedingJoinPoint call) throws JavaCallTerminationException {
		AbstractJavaCallTerminatorController terminatorController =
			(AbstractJavaCallTerminatorController)getController();
		
		boolean terminate = terminatorController.terminate(call);
		
		// in case of termination decision an exception is thrown which is manifesting the termination event
		if (terminate) {
			throw new JavaCallTerminationException("The call was terminated due to controller decission");
		}
		
		// note: if could be also possible to not thrown an exception and terminate in silent mode, 
		//       this approach could be seen as imperceptibly modifying functionality!
		
		return terminate;
	}

	@Override
	String controllerClassName() {
		//return getClass().getName(); // advice class name
		return getController().getClass().getName(); // configured ('controllerRef') controller class in use
	}

	@Override
	String controllerMethodName() {
		return "terminate";
	}
	
}
