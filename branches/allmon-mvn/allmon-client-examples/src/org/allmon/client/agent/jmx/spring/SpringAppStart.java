package org.allmon.client.agent.jmx.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * http://www.codercorp.com/blog/tips-and-tricks/monitoring-spring-based-applications-hibernate-statistics-using-jmx-example.html
 * 
 * necessary VM arguments :
 * -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port="9004"
 * -Dcom.sun.management.jmxremote.authenticate="false"
 * -Dcom.sun.management.jmxremote.ssl="false"
 * 
 */
public class SpringAppStart {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"org/allmon/client/agent/jmx/spring/spring-jmx.xml");
		System.out.println("SpringAppStart is up...");
		while (true) {
			try {
				Thread.sleep(10000);
				System.out.println("...and running...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
