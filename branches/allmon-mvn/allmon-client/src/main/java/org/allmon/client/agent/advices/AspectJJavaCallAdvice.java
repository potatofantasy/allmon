package org.allmon.client.agent.advices;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * To use this advice you must:<br>
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
	        <concrete-aspect name="org.allmon.client.agent.aop.annotations.advice.ConcreteAspectJAdvice"
	                         extends="org.allmon.client.agent.aop.annotations.advice.AbstractAspectJAdvice">
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
public abstract class AspectJJavaCallAdvice extends AbstractJavaCallAdvice {

	private static final Log logger = LogFactory.getLog(AspectJJavaCallAdvice.class);
	
	public AspectJJavaCallAdvice() {
		setName("AspectJJavaCallAdvice");
		logger.debug("AspectJJavaCallAdvice created");
		agentContext = new AgentContext(); // hard-coded creation of agent context instance
	}
	
	@Pointcut
	public abstract void pointcutMethod();
	
	@AfterThrowing(pointcut="pointcutMethod() && target(ta)", argNames="jp,th,ta", throwing="th") 
	public final void profileThrowing(ProceedingJoinPoint jp, Throwable th, Object ta) {
		//System.out.println("throwing >>> " + th.getMessage() + ", target:" + ta.toString());
		JavaCallAgent agent = null;
		try {
			MetricMessage metricMessage = createMetricMessage((JoinPoint)jp);
			agent = new JavaCallAgent(agentContext, metricMessage);
	        agent.exitPoint(th);
    	} catch (Throwable t) {
    	}
	}
	
	@Around(value="pointcutMethod()")
	public Object profileAspectJ(ProceedingJoinPoint call) throws Throwable {
		return profile(call);
	}
	
}