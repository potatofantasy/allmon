package org.allmon.client.agent.aop.basic.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringHelloWorldController {

    //public final static AgentContext agentContext = new AgentContext(); 
	// FIXME flushing problem!!! make this not static

    private final static String SPRING_CONFIG_LOCATION = "/org/allmon/client/agent/aop/basic/controller/spring-config.xml";
    
    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
        HelloWorldInterface bean = (HelloWorldInterface) applicationContext.getBean("MessageBean");
            	
    	//HelloWorldInterface bean = new HelloWorldImpl();
        for (int i = 0; i < 2; i++) {
        	System.out.println("Controller is calling Hello World [no:" + i + "] >>> ");
            bean.printMessage();
        }
    }

}