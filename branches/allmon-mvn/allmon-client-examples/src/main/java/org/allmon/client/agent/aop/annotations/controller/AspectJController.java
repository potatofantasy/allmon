package org.allmon.client.agent.aop.annotations.controller;

import org.allmon.client.agent.aop.basic.controller.HelloWorldImpl;

/**
 * Add -javaagent property to VM arguments:
 * 
 * -javaagent:absolute_path/aspectjweaver.jar
 * 
 */
public class AspectJController {

	public static void main(String[] args) {
		HelloWorldImpl bean = new HelloWorldImpl();
		bean.setMessage("Some Message");

		for (int i = 0; i < 3; i++) {
			bean.printMessage();
		}
	}
	
}
