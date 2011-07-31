package org.allmon.client.agent.namespace;

public interface AllmonAgentBeanDefinitionParserFactory {

	String NOT_SUPPORTED_FUNCTIONALITY = "This functionality is not supported";
	
	AgentBeanDefinitionElementParser getParser(String localName);
	
}
