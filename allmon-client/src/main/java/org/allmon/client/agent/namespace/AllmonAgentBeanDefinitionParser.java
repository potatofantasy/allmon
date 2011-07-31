package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract class AllmonAgentBeanDefinitionParser implements BeanDefinitionParser {

	private final ParseState parseState = new ParseState();
	
	public final ParseState getParseState() {
		return parseState;
	}
	
	protected void parseElementNodes(Element element, ParserContext parserContext, 
			AllmonAgentBeanDefinitionParserFactory factory) {
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String localName = node.getLocalName();
				
				AgentBeanDefinitionElementParser parser = factory.getParser(localName);
				
				parser.setParser(this);
				parser.parse((Element) node, parserContext);
			}
		}

		parserContext.popAndRegisterContainingComponent();
	}
	
}
