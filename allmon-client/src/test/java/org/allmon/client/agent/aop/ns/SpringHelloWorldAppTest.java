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

		Thread.sleep(3000);
	}

	private void method(HelloWorldImpl bean) throws InterruptedException {
//		for (int i = 0; i < 100; i++) {
			bean.printMessage();
			bean.printMessage("param");
			bean.printMessage(new String[]{"param1", "param2"});
			bean.printMessageE();
			//Thread.sleep(100);
//		}
		Thread.sleep(3000);
		for (int i = 0; i < 2; i++) {
			bean.printMessage();
		}
	}

}
