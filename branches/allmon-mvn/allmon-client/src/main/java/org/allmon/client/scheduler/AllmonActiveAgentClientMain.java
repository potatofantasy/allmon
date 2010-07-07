package org.allmon.client.scheduler;

import java.io.File;
import java.io.IOException;

import org.allmon.common.AllmonPropertiesReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AllmonActiveAgentClientMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
	public static final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
			new String[] { "classpath:activeAgentAppContext.xml" }); 
	
	public static void main(String[] args) {

		try {
			System.out.println("Reading config file in path " + new File(".").getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("starting job"); // TODO clean up the logging
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("end.");

	}

}
