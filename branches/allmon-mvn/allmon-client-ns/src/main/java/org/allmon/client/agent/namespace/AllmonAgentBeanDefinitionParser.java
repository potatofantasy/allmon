package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

abstract class AllmonAgentBeanDefinitionParser implements BeanDefinitionParser {

	private final ParseState parseState = new ParseState();
	
	public final ParseState getParseState() {
		return parseState;
	}
	
}
