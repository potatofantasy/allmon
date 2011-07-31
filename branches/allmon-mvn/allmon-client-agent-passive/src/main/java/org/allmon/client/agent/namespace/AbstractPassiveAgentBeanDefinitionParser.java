package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

abstract class AbstractPassiveAgentBeanDefinitionParser extends AbstractAgentBeanDefinitionElementParser {

	protected PassiveAgentBeanDefinitionParser parser;
	//protected AllmonAgentBeanDefinitionParser parser;

	@Override
	public void setParser(AllmonAgentBeanDefinitionParser parser) {
		this.parser = (PassiveAgentBeanDefinitionParser)parser;
	}
	
	@Override
	final PassiveAgentBeanDefinitionParser getParser() {
		return parser;
	}
	
//	public AbstractPassiveAgentBeanDefinitionParser(PassiveAgentBeanDefinitionParser parser, String tagName) {
////		super(parser, tagName);
////		this.parser = parser;
//	}
	
	protected abstract void parseSpecifics(Element agentElement, ParserContext parserContext);
	
	public final void parse(Element agentElement, ParserContext parserContext) {
		String id = agentElement.getAttribute(ID);
		try {
			parser.getParseState().push(new PassiveAgentEntry(id));
			parseSpecifics(agentElement, parserContext);
		} finally {
			parser.getParseState().pop();
		}
	}
	
}