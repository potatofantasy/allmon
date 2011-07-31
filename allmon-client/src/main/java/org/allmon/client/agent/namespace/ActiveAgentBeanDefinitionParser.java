package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.w3c.dom.Element;

public final class ActiveAgentBeanDefinitionParser extends AllmonAgentBeanDefinitionParser {

	private static final ClassPathXmlApplicationContext context = 
		new ClassPathXmlApplicationContext(
				new String[] { "classpath:META-INF/allmonAgentAppContext-activeNamespaceHandler.xml" });
	
	private static final String AGENT_SCHEDULER = "agentScheduler";
	
	private BeanDefinition activeAgentSchedulerDef;
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		CompositeComponentDefinition compositeDef =
				new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compositeDef);

		activeAgentSchedulerDef = getActiveAgentScheduler(parserContext);
		
		AllmonAgentBeanDefinitionParserFactory factory = 
			(AllmonAgentBeanDefinitionParserFactory)context.getBean("activeAgentBeanDefinitionParserFactory");
		
		parseElementNodes(element, parserContext, factory);
		
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
