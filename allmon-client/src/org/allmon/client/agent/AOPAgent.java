package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect

//TODO: This is an embedded agent, not external hence needs some more looking into for Agent Context Buffer
public class AOPAgent extends PassiveAgent {

	private long timeStarted;

	
	AOPAgent(AgentContext agentContext, MetricMessage metricMessage) {
		super(agentContext, metricMessage);
		// TODO Auto-generated constructor stub
	}

	//TODO: add information about the target object and called method.
	//TODO: Add information about parameter types and values and exceptions thrown if any
	@Around("monitor()")
	public Object addMetrics(ProceedingJoinPoint pjp) throws Throwable {
		try {
			timeStarted = System.currentTimeMillis();

			return pjp.proceed();
		} finally {
			long durationTime = System.currentTimeMillis() - timeStarted;

			MetricMessage metricMessage = MetricMessageFactory.createClassMessage(pjp.getTarget().getClass().getSimpleName(), 
					pjp.getSignature().getName(), null, null, durationTime);

			addMetricMessage(metricMessage);
		}
	}

	//Pointcut Expression goes here
	//TODO: Need to parameterize this expression - either through Spring or through properties files or both
	//@Pointcut("execution(public * org.allmon.client.agent.aop.services..*.*(..))")
	@Pointcut("execution()")//use above as example
	public void monitor() {

	}

}
