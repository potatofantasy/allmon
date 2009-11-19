package org.allmon.client.agent.aop;

import org.allmon.client.agent.AgentContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringHelloWorldController {

    public final static AgentContext agentContext = new AgentContext(); // FIXME flushing problem!!! make this not static

    private final static String SPRING_CONFIG_LOCATION = "org/allmon/client/agent/aop/spring-config.xml";
    
    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
        HelloWorldInterface bean = (HelloWorldInterface) applicationContext.getBean("MessageBean");
        
        for (int i = 0; i < 10; i++) {
            bean.printMessage();
        }
        
        System.out.println("End");
        agentContext.stop();
        System.out.println("Exit");
    }

}