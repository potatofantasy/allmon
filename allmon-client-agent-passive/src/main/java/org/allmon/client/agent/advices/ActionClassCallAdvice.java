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
		<allmon:actionClassAgent id="id1" agentContextRef="agentContext" 
			...
	</allmon:passive>
 * }</pre>
 * 
 */
public class ActionClassCallAdvice extends AbstractActionClassCallAdvice {

	private static final Log logger = LogFactory.getLog(ActionClassCallAdvice.class);
	
	public ActionClassCallAdvice() {
		logger.debug("ActionClassCallAdvice created - name " + getName());
	}
	
	public void pointcutMethod() {
	}
	
}