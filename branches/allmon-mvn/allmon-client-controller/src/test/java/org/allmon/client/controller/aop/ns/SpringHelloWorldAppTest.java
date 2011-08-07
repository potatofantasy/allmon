package org.allmon.client.controller.aop.ns;

import junit.framework.TestCase;

import org.allmon.client.controller.aop.ns.HelloWorldImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringHelloWorldAppTest extends TestCase {
	
	private final static String SPRING_CONFIG_LOCATION = "org/allmon/client/controller/aop/ns/spring-config-ns.xml";

	public void testMain() throws InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
		HelloWorldImpl bean = (HelloWorldImpl) applicationContext.getBean("messageBean");

		SpringHelloWorldAppTest app = new SpringHelloWorldAppTest();
		assertNotNull(app);
		String messages = app.method(bean);
		assertNotNull(messages);
		//assertEquals("Hello World message ....Hello World message ....:paramHello World message ....:[Ljava.lang.String;@d1e7c2Hello World message ....Hello World message ....", messages);
		
		RuntimeException re = null;
		try {
			app.methodWithException(bean);
		} catch (RuntimeException re2) {
			re = re2;
		}
		assertNotNull(re);
		
		Thread.sleep(2000);
	}

	private String method(HelloWorldImpl bean) throws InterruptedException {
		System.out.println("SpringHelloWorldAppTest - method");
		String messages = "";
		messages += bean.printMessage();
		messages += bean.printMessage("param");
		messages += bean.printMessage(new String[]{"param1", "param2"});
		for (int i = 0; i < 2; i++) {
			messages += bean.printMessage();
		}
		//
		try {
			messages += bean.printMessage("param1", "param2");
			fail("Controll action has not been taken; methods with over one parameter shoud be terminated");
		} catch (Exception t) {
		}
		return messages;
	}

	private void methodWithException(HelloWorldImpl bean) throws InterruptedException {
		System.out.println("SpringHelloWorldAppTest - methodWithException");
		bean.printMessageE();
	}
	
}
