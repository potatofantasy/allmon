package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AllmonNamespaceHandler extends NamespaceHandlerSupport {
    
    public void init() {
        registerBeanDefinitionParser("agentContext", new AgentContextBeanDefinitionParser());        
        
        // TODO this should be instantiated based on spring bean config
        registerBeanDefinitionParser("active", null); //new ActiveAgentBeanDefinitionParser());
        registerBeanDefinitionParser("passive", null); //new PassiveAgentBeanDefinitionParser());
    }
    
}