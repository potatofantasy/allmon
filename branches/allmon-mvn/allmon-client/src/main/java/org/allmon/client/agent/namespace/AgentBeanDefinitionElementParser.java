package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public interface AgentBeanDefinitionElementParser {

	void setParser(AllmonAgentBeanDefinitionParser parser);
	
	void parse(Element agentElement, ParserContext parserContext);
	
}
