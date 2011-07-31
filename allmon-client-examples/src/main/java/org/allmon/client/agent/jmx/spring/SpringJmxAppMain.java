package org.allmon.client.agent.jmx.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * http://www.codercorp.com/blog/tips-and-tricks/monitoring-spring-based-applications-hibernate-statistics-using-jmx-example.html
 * 
 * necessary VM arguments :
 * -Dcom.sun.management.jmxremote 
 * -Dcom.sun.management.jmxremote.port="9004"
 * -Dcom.sun.management.jmxremote.authenticate="false"
 * -Dcom.sun.management.jmxremote.ssl="false"
 * 
 */
public class SpringJmxAppMain {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"org/allmon/client/agent/jmx/spring/spring-jmx.xml");
		JmxTestBean bean = (JmxTestBean) context.getBean("testBean");

		System.out.println("SpringJmxAppMain is up...");
		while (true) {
			try {
				Thread.sleep(5000);
				System.out.print("...and running...");
				System.out.println(bean.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
