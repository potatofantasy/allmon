package org.allmon.client.controller.namespace;

import org.allmon.client.agent.namespace.AllmonAgentBeanDefinitionParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ControllerBeanDefinitionParser implements BeanDefinitionParser {

	private static final Log logger = LogFactory.getLog(ControllerBeanDefinitionParser.class);
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

//		activeAgentScheduletDef = getActiveAgentScheduler(parserContext);
		
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String localName = node.getLocalName();
				
				ControllerBeanDefinitionParserFactory factory = new ControllerBeanDefinitionParserFactory();
				AbstractControllerBeanDefinitionParser parser = factory.getParser(localName);
				
				parser.setParser(this);
				parser.parse((Element) node, parserContext);
			}
		}

		parserContext.popAndRegisterContainingComponent();
		return null;
	}
	
	
	// from AllmonAgentBeanDefinitionParser

	private final ParseState parseState = new ParseState();
	
	public final ParseState getParseState() {
		return parseState;
	}
	
	protected void parseElementNodes(Element element, ParserContext parserContext, 
			AllmonAgentBeanDefinitionParserFactory factory) {
		
	}
}

class PassiveAgentEntry implements ParseState.Entry {

	private final String name;

	public PassiveAgentEntry(String name) {
		this.name = name;
	}

	public String toString() {
		return "Agent '" + this.name + "'";
	}

}