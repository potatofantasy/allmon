package org.allmon.client.agent.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringHelloWorldController {

    public static void main(String[] args) {
        String configLocations = new String("org/allmon/client/agent/aop/spring-config.xml");

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocations);
        HelloWorldInterface bean = (HelloWorldInterface) applicationContext.getBean("MessageBean");
        bean.printMessage();
        
        System.out.println("End.");
    }

}