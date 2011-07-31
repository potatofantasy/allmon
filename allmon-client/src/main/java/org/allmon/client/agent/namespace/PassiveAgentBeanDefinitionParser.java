package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PassiveAgentBeanDefinitionParser extends AllmonAgentBeanDefinitionParser {

	private static final ClassPathXmlApplicationContext context = 
		new ClassPathXmlApplicationContext(
				new String[] { "classpath:META-INF/allmonAgentAppContext-passiveNamespaceHandler.xml" });
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

//		activeAgentScheduletDef = getActiveAgentScheduler(parserContext);
		
		AllmonAgentBeanDefinitionParserFactory factory = 
			(AllmonAgentBeanDefinitionParserFactory)context.getBean("passiveAgentBeanDefinitionParserFactory");
		
		parseElementNodes(element, parserContext, factory);
		
		return null;
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