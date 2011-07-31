package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class is allmon namespace handler - 
 * 
 */
public class AllmonNamespaceHandler extends NamespaceHandlerSupport {
    
//	private static final ClassPathXmlApplicationContext context = 
//		new ClassPathXmlApplicationContext(
//				new String[] { "classpath:META-INF/allmonAgentAppContext-namespaceHandler.xml" });
	
//	// TODO replace with annotations
//	private static final BeanDefinitionParser activeAgentBeanDefinitionParser = 
//		(BeanDefinitionParser)context.getBean("activeAgentBeanDefinitionParser");
	
//	// TODO replace with annotations
//	private static final BeanDefinitionParser passiveAgentBeanDefinitionParser = 
//		(BeanDefinitionParser)context.getBean("passiveAgentBeanDefinitionParser");
	
    public void init() {
		registerBeanDefinitionParser("agentContext", new AgentContextBeanDefinitionParser());

//        registerBeanDefinitionParser("active", activeAgentBeanDefinitionParser); //new ActiveAgentBeanDefinitionParser());
//        registerBeanDefinitionParser("passive", passiveAgentBeanDefinitionParser); //new PassiveAgentBeanDefinitionParser());
		registerBeanDefinitionParser("active", new ActiveAgentBeanDefinitionParser());
//		registerBeanDefinitionParser("passive", new PassiveAgentBeanDefinitionParser());
	}
    
}