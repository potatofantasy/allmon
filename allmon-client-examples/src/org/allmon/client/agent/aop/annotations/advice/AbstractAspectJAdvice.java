package org.allmon.client.agent.aop.annotations.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class AbstractAspectJAdvice {

	@Around("pointcutMethod()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		long timeStarted = 0;
		try {
			timeStarted = System.nanoTime();

			return pjp.proceed();
		} finally {
			long durationTime = System.nanoTime() - timeStarted;
			
			String className = pjp.getSignature().getDeclaringTypeName();
			String methodName = pjp.getSignature().getName();
			System.out.println("AspectJAdvice >> " + className + " " + methodName + " >> " + durationTime);
		}
	}

//	@Pointcut(pointucExpression)
//	public void pointcutMethods() {}

	@Pointcut
	public abstract void pointcutMethod();

	
	//     precedence="org.allmon.client, *"
}
