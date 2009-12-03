package org.allmon.client.agent.aop.controllers;

import org.allmon.client.agent.aop.services.HelloWorldInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringHelloWorldController {

    //public final static AgentContext agentContext = new AgentContext(); 
	// FIXME flushing problem!!! make this not static

    private final static String SPRING_CONFIG_LOCATION = "org/allmon/client/agent/aop/controllers/spring-config.xml";
    
    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
        HelloWorldInterface bean = (HelloWorldInterface) applicationContext.getBean("MessageBean");
        
    	
    	//HelloWorldInterface bean = new SpringHelloWorld();
        for (int i = 0; i < 1; i++) {
        	//System.out.println(i);
            bean.printMessage();
        }
    }

}