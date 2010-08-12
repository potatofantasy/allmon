package org.allmon.client.agent.aop.aspectj;

import junit.framework.TestCase;

import org.allmon.client.agent.aop.HelloWorldImpl;

/**
 * 0) Due to http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=eb3fcd8f72ab4713f96e378a7575?bug_id=6614974
 * you may have to add -noverify to VM args
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
 */
public class PojoHelloWorldAppTest extends TestCase {
		
	public void testMain() throws InterruptedException {
		System.out.println("PojoHelloWorldAppTest - testMain");
		HelloWorldImpl bean = new HelloWorldImpl();
		bean.setMessage("Pojo Hello World Application...");
		//bean.setSilentMode(false); // true = no system out
		
		PojoHelloWorldAppTest app = new PojoHelloWorldAppTest();
		app.method(bean);
		assertTrue(true);
		
		RuntimeException re = null;
		try {
			app.methodWithException(bean);
		} catch (RuntimeException re2) {
			re = re2;
		}
		assertNotNull(re);
		
		Thread.sleep(3000);
	}

	private void method(HelloWorldImpl bean) throws InterruptedException {
		System.out.println("PojoHelloWorldAppTest - method");
		//for (int i = 0; i < 100; i++) {
			bean.printMessage();
			bean.printMessage("param");
			bean.printMessage(new String[]{"param1", "param2"});
			Thread.sleep(500);
		//}
		Thread.sleep(3000);
		for (int i = 0; i < 2; i++) {
			bean.printMessage();
		}
	}

	private void methodWithException(HelloWorldImpl bean) {
		System.out.println("PojoHelloWorldAppTest - methodWithException");
		bean.printMessageE();
	}
	
}
