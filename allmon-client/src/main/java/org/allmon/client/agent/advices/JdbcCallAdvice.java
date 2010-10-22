package org.allmon.client.agent.advices;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * This class is used internally by allmon passive monitoring Spring namespace engine.
 * Can be easily applied to spring beans by following code:
 * 
 * <pre>{@code
 *  <allmon:passive>
		<allmon:jdbcCallAgent id="id1" agentContextRef="agentContext" 
			...
	</allmon:passive>
 * }</pre>
 * 
 * 
 * <br><br>
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
	        <include within="..*" />
	    </weaver>
	    <aspects>
	        <!-- define a jdbc aspect for weaving -->
	        <aspect name="org.allmon.client.agent.advices.JdbcCallAdvice"/>
	    </aspects>
	</aspectj>
 * }</pre>
 * 
 * More info about how to use AspectJ Load-Time Weaving can be found here:
 * http://www.eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html
 * 
 */
@Aspect
public class JdbcCallAdvice extends AbstractJavaCallAdvice {

	private final Log logger = LogFactory.getLog(JdbcCallAdvice.class);

	public JdbcCallAdvice() {
		logger.debug("JdbcCallAdvice created - name " + getName());
		agentContext = new AgentContext(); // hard-coded creation of agent context instance
	}
		
	@Pointcut("call (* java.sql..*.execute*(..))")
	public void pointcutMethod() {
	}
	
	@AfterThrowing(pointcut="pointcutMethod() && target(ta)", argNames="jp,th,ta", throwing="th") 
	public final void profileThrowing(ProceedingJoinPoint jp, Throwable th, Object ta) {
		//System.out.println("throwing >>> " + th.getMessage() + ", target:" + ta.toString());
		JavaCallAgent agent = null;
		try {
			MetricMessage metricMessage = createMetricMessage((JoinPoint)jp);
			agent = new JavaCallAgent(agentContext, metricMessage);
	        agent.exitPoint(th);
    	} catch (Throwable t) {
    		logger.error("Error occured while creating JavaCallAgent exit(afterThrowing) MetricMessage: " + t.getMessage(), t);
    	}
	}
	
	@Around(value="pointcutMethod()")
	public Object profileAspectJ(ProceedingJoinPoint call) throws Throwable {
		return profile(call);
	}
	

	protected MetricMessage createMetricMessage(JoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		if (isVerboseMode()) {
			logger.debug("profile >>> " + className + "." + methodName);
		}
		
		// getting caller class.method of the advised method
		Caller caller = new Caller();
		if (isFindCaller()) {
			caller.getOriginalCaller(className, methodName);
		}
		
		MetricMessage metricMessage = MetricMessageFactory.createClassMessage(
                className, methodName, caller.className, caller.methodName);
		
		// acquiring call parameters
		if (isAcquireCallParameters()) {
			metricMessage.setParameters(call.getArgs());
		}
		
		return metricMessage;
	}
}