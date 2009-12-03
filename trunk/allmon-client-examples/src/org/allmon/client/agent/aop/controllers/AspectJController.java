package org.allmon.client.agent.aop.controllers;

import org.allmon.client.agent.aop.services.SpringHelloWorld;

public class AspectJController {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringHelloWorld bean = new SpringHelloWorld();
		bean.setMessage("Some Message");

		//HelloWorldInterface bean = new SpringHelloWorld();
		for (int i = 0; i < 1; i++) {
			bean.printMessage();
		}
	}
}
