package org.allmon.client.controller.namespace;

import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

abstract class AbstractControllerBeanDefinitionParser extends AbstractAllconBeanDefinitionElementParser {

	protected ControllerBeanDefinitionParser parser;

//	@Override
	public void setParser(ControllerBeanDefinitionParser parser) {
		this.parser = parser;
	}
	
	@Override
	final ControllerBeanDefinitionParser getParser() {
		return parser;
	}
	
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