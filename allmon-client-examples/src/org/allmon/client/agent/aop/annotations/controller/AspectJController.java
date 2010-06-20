package org.allmon.client.agent.aop.annotations.controller;

import org.allmon.client.agent.aop.basic.controller.HelloWorldImpl;

public class AspectJController {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HelloWorldImpl bean = new HelloWorldImpl();
		bean.setMessage("Some Message");

		//HelloWorldInterface bean = new HelloWorldImpl();
		for (int i = 0; i < 1; i++) {
			bean.printMessage();
		}
	}
}
