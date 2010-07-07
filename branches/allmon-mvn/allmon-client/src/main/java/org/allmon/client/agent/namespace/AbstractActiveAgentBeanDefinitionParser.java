package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

abstract class AbstractActiveAgentBeanDefinitionParser extends AbstractAllmonAgentBeanDefinitionParser {

	protected final ActiveAgentBeanDefinitionParser parser;
	
	public AbstractActiveAgentBeanDefinitionParser(ActiveAgentBeanDefinitionParser parser, String tagName) {
		super(parser, tagName);
		this.parser = parser;
	}
	
	protected abstract void parseSpecifics(Element agentElement, ParserContext parserContext);
	
	public final void parse(Element agentElement, ParserContext parserContext) {
		String id = agentElement.getAttribute(ID);
		try {
			parser.getParseState().push(new ActiveAgentEntry(id));
			parseSpecifics(agentElement, parserContext);
		} finally {
			parser.getParseState().pop();
		}
	}
	
}
