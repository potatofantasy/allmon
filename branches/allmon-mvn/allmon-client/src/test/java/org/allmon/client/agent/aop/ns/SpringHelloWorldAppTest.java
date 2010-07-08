package org.allmon.client.agent.aop.ns;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringHelloWorldAppTest extends TestCase {
	
	private final static String SPRING_CONFIG_LOCATION = "org/allmon/client/agent/aop/ns/spring-config-ns.xml";

	public void testMain() throws InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
		HelloWorldImpl bean = (HelloWorldImpl) applicationContext.getBean("messageBean");

		SpringHelloWorldAppTest app = new SpringHelloWorldAppTest();
		app.method(bean);
		assertTrue(true);
		
		RuntimeException re = null;
		try {
			app.methodWithException(bean);
		} catch (RuntimeException re2) {
			re = re2;
		}
		assertNotNull(re);
		
		Thread.sleep(2000);
	}

	private void method(HelloWorldImpl bean) throws InterruptedException {
		System.out.println("SpringHelloWorldAppTest - method");
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

	private void methodWithException(HelloWorldImpl bean) throws InterruptedException {
		System.out.println("SpringHelloWorldAppTest - methodWithException");
		bean.printMessageE();
	}
	
}
