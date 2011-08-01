package org.allmon.client.agent.namespace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Element;

public class PassiveAgentBeanDefinitionParser extends AllmonAgentBeanDefinitionParser {

	private static final Log logger = LogFactory.getLog(PassiveAgentBeanDefinitionParser.class);
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

//		activeAgentScheduletDef = getActiveAgentScheduler(parserContext);
		
		logger.debug("Parsing passive element definition - using PassiveAgentBeanDefinitionParserFactory instance");
		// depending on active element declared in xml use PassiveAgentBeanDefinitionParserFactory
		ClassPathXmlApplicationContext context = 
			new ClassPathXmlApplicationContext(
					new String[] { "classpath:META-INF/allmonAgentAppContext-passiveNamespaceHandler.xml" });
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