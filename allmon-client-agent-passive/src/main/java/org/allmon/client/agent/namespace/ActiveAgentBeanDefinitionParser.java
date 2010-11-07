package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ActiveAgentBeanDefinitionParser extends AllmonAgentBeanDefinitionParser {

	private static final String AGENT_SCHEDULER = "agentScheduler";
	
	// TODO move those tags names to specific AllmonAgentBeanDefinitionParser
	private static final String HTTP_URL_CALL_AGENT = "httpUrlCallAgent";
	private static final String JMX_SERVER_AGENT = "jmxServerAgent";
	
	
	private BeanDefinition activeAgentSchedulerDef;
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

		activeAgentSchedulerDef = getActiveAgentScheduler(parserContext);
		
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String localName = node.getLocalName();
				AbstractActiveAgentBeanDefinitionParser parser = null;
				if (HTTP_URL_CALL_AGENT.equals(localName)) {
					parser = new HttpUrlCallAgentBeanDefinitionParser(this, HTTP_URL_CALL_AGENT);
				} else if (JMX_SERVER_AGENT.equals(localName)) {
					parser = new JmxServerAgentBeanDefinitionParser(this, JMX_SERVER_AGENT);
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

	public BeanDefinition getActiveAgentSchedulerDef() {
		return activeAgentSchedulerDef;
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
