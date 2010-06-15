package org.allmon.client.scheduler;

import java.io.File;
import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AllmonActiveAgentClient {

	public static final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
			new String[] { "classpath:resources/applicationContext.xml" }); 
	
	public static void main(String[] args) {

		try {
			System.out.println("Reading config file in path " + new File(".").getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("starting job");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("end.");

	}

}
