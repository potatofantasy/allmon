package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * This class is allmon namespace handler.
 * 
 */
public class AllmonNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("agentContext", new AgentContextBeanDefinitionParser());

		registerBeanDefinitionParser("active", new ActiveAgentBeanDefinitionParser());
		registerBeanDefinitionParser("passive", new PassiveAgentBeanDefinitionParser());
	}

}