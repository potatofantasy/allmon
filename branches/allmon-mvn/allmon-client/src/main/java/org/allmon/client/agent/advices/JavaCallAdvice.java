package org.allmon.client.agent.advices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This advice can be use together with Spring AOP.
 * 
 * This class is used internally by allmon passive monitoring Spring namespace engine.
 * Can be easily applied to spring beans by following code:
 * 
 * <pre>{@code
 *  <allmon:passive>
		<allmon:javaCallAgent id="id1" agentContextRef="agentContext" 
			...
	</allmon:passive>		
 * }</pre>
 * 
 */
public class JavaCallAdvice extends AbstractJavaCallAdvice { //AspectJJavaCallAdvice {
	
	private static final Log logger = LogFactory.getLog(JavaCallAdvice.class);
	
	public JavaCallAdvice() {
		//System.out.println("JavaCallAdvice created");
		logger.debug("JavaCallAdvice created - name " + getName());
	}
	
}
