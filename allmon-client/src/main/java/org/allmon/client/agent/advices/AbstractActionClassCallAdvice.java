package org.allmon.client.agent.advices;

import javax.servlet.http.HttpServletRequest;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * To use directly this advice for <b>struts action classes instances</b> you must:<br>
 * 
 * 1) add -javaagent property to VM arguments:
 * <pre>
 * -javaagent:absolute_path/aspectjweaver.jar
 * </pre>
 * <br>
 * 2) add to your classpath META-INF directory an AspectJ descriptor (META-INF/aop.xml),
 * with pointcut expression specifying all your required classes to instrument: 
 * 
 * <pre>{@code
	<aspectj>
	    <weaver options="-verbose -showWeaveInfo">
	        <!-- only weave classes in this package -->
	        <include within="org.allmon.client.agent.aop..*" />
	    </weaver>
	    <aspects>
	        <!-- define a concrete aspect inline for weaving -->
	        <concrete-aspect name="org.allmon.client.agent.aop.annotations.advice.ConcreteActionClassCallAdvice"
	                         extends="org.allmon.client.agent.advices.AbstractActionClassCallAdvice">
	        	<pointcut name="pointcutMethod" expression="execution(public * org.allmon.client.agent.aop..*.*(..))"/>
	        </concrete-aspect>        
	    </aspects>
	</aspectj>
 * }</pre>
 * 
 * More info about how to use AspectJ Load-Time Weaving can be found here:
 * http://www.eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html
 * 
 */
@Aspect
public abstract class AbstractActionClassCallAdvice extends AspectJJavaCallAdvice {
	
	private final Log logger = LogFactory.getLog(AbstractActionClassCallAdvice.class);
	
	private String sessionUserAttributeKey = ""; // FIXME add parametrization !!!
	
	public AbstractActionClassCallAdvice() {
		logger.debug("AbstractActionClassCallAdvice created");
	}
	
	@Pointcut
	abstract public void pointcutMethod();
	
	protected MetricMessage createMetricMessage(JoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		//String methodName = call.getSignature().getName();
		
		HttpServletRequest request = null;
		String webSessionId = null;
		String user = "";
		//if (isAcquireCallParameters()) { // TODO throughly review setting params for this advice
			try {
				Object [] args = call.getArgs();
				// taking the third parameter (HttpServletRequest) from struts action calls
				// ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response)
				request = (HttpServletRequest)args[2];
				// taking session id 
				webSessionId = request.getRequestedSessionId();
				// taking user object
		        if (!"".equals(sessionUserAttributeKey)) {
		        	try {
		        		HttpServletRequestUtil util = new HttpServletRequestUtil((HttpServletRequest)request);
		        		//if (serializeUserObject) {
		        			user = util.getUserObjectSerializedString(sessionUserAttributeKey);
		        		//} else {
		            	//	user = util.getUserObjectString(sessionUserAttributeKey);
		        		//}
		        	} catch (Exception ex) {
		        		user = "user-not-found";
		        	}
		        }
			} catch (Exception ex) {
				logger.error("Error occured while creating JavaCallAgent entry MetricMessage: " + ex.getMessage(), ex);
			}
		//}
		
		MetricMessage metricMessage = MetricMessageFactory.createActionClassMessage(
				className, user, webSessionId, request);
		// Note: setting parameters is not needed on this level - all happens in while message is created
		// X X X metricMessage.setParameters(args);
		return metricMessage;
	}

	public void setSessionUserAttributeKey(String sessionUserAttributeKey) {
		this.sessionUserAttributeKey = sessionUserAttributeKey;
	}
	
	public String getSessionUserAttributeKey() {
		return sessionUserAttributeKey;
	}
	
}
