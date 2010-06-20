package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ActiveAgentBeanDefinitionParser implements BeanDefinitionParser {

	private static final String AGENT_SCHEDULER = "agentScheduler";
	
	private static final String HTTP_URL_CALL_AGENT = "httpUrlCallAgent";
	

	private ParseState parseState = new ParseState();
	
	private BeanDefinition activeAgentScheduletDef;
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

		activeAgentScheduletDef = getActiveAgentScheduler(parserContext);
		
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String localName = node.getLocalName();
				AbstractActiveAgentBeanDefinitionParser parser = null;
				if (HTTP_URL_CALL_AGENT.equals(localName)) {
					parser = new HttpUrlCallAgentBeanDefinitionParser(this);
				} else {
					// TODO add others
				}
				parser.parse((Element) node, parserContext);
			}
		}

		parserContext.popAndRegisterContainingComponent();
		return null;
	}

	private BeanDefinition getActiveAgentScheduler(ParserContext parserContext) {
		BeanDefinition activeAgentScheduletDef;
		if (!parserContext.getRegistry().isBeanNameInUse(AGENT_SCHEDULER)) {
			activeAgentScheduletDef = new RootBeanDefinition(SchedulerFactoryBean.class);
			parserContext.getRegistry().registerBeanDefinition(AGENT_SCHEDULER, activeAgentScheduletDef);
		} else {
			activeAgentScheduletDef = parserContext.getRegistry().getBeanDefinition(AGENT_SCHEDULER);
		}
		return activeAgentScheduletDef;
	}

	public ParseState getParseState() {
		return parseState;
	}

	public BeanDefinition getActiveAgentScheduletDef() {
		return activeAgentScheduletDef;
	}
	
}

class ActiveAgentEntry implements ParseState.Entry {

	private final String name;

	public ActiveAgentEntry(String name) {
		this.name = name;
	}

	public String toString() {
		return "Agent '" + this.name + "'";
	}

}
