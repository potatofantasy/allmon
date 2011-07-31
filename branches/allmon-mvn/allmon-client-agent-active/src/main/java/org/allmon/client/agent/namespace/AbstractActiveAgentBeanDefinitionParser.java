package org.allmon.client.agent.namespace;

import org.allmon.client.agent.ActiveAgentCaller;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

abstract class AbstractActiveAgentBeanDefinitionParser extends AbstractAgentBeanDefinitionElementParser {

	private static final String CRON_EXPRESSION = "cronExpression";
	
	protected ActiveAgentBeanDefinitionParser parser;
	//protected AllmonAgentBeanDefinitionParser parser;

	@Override
	public void setParser(AllmonAgentBeanDefinitionParser parser) {
		this.parser = (ActiveAgentBeanDefinitionParser)parser;
	}
	
	@Override
	final ActiveAgentBeanDefinitionParser getParser() {
		return parser;
	}
	
	private static int instanceCounter = 0;
		
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
	
	/**
	 * 1. create a new active agent object
	 * 2. parse object id and agent context fields
	 * 3. parse cron jobs context and create new triggers
	 * 
	 * @param agentElement
	 * @param parserContext
	 * @param activeAgentClass
	 * @return
	 */
	protected RootBeanDefinition parseActiveAgentContext(Element agentElement, ParserContext parserContext, Class activeAgentClass) {
		// FIXME validate activeAgentClass types 
		
		RootBeanDefinition agentDef = new RootBeanDefinition(activeAgentClass);
		
		// parse object id and agent context fields
		parseObjectContext(agentElement, parserContext, agentDef);
				
		// parse cron jobs context and create new triggers
		parseCronJobsContext(agentElement, parserContext, agentDef);
		
		return agentDef;
	}
	
	/**
	 * Parse object id and agent context fields
	 * 
	 * @param agentElement
	 * @param parserContext
	 * @param activeAgentDef
	 */
	private void parseObjectContext(Element agentElement, ParserContext parserContext, RootBeanDefinition activeAgentDef) {
		String agentBeanName = agentElement.getAttribute(ID);
		if (StringUtils.hasText(agentBeanName)) {
			parserContext.getRegistry().registerBeanDefinition(agentBeanName, activeAgentDef);
		} else {
			agentBeanName = parserContext.getReaderContext().registerWithGeneratedName(activeAgentDef);
		}

		// TODO move agentContext reference
		Object agentContext = parseAgentContextProperty(agentElement, parserContext);
		if (agentContext instanceof BeanDefinition) {
			activeAgentDef.getConstructorArgumentValues().addGenericArgumentValue(agentContext);
		}
		else if (agentContext instanceof String) {
			activeAgentDef.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference((String)agentContext));
		}
	}
	
	/**
	 * Parse cron jobs context and create new triggers
	 * 
	 * @param agentElement
	 * @param parserContext
	 * @param activeAgentDef
	 */
	private void parseCronJobsContext(Element agentElement, ParserContext parserContext, RootBeanDefinition activeAgentDef) {

		// generate names
		String jobDetailBean = "jobDetailBean-" + instanceCounter;
		String cronTriggerBean = "cronTriggerBean-" + instanceCounter;
		
		instanceCounter++;
		
		// job
		ManagedMap managedMap = new ManagedMap();
		managedMap.put("activeAgent", activeAgentDef);
		RootBeanDefinition callerJobDef = new RootBeanDefinition(JobDetailBean.class);
		callerJobDef.getPropertyValues().addPropertyValue("jobClass", ActiveAgentCaller.class.getName());
		callerJobDef.getPropertyValues().addPropertyValue("jobDataAsMap", managedMap);
		parserContext.getRegistry().registerBeanDefinition(jobDetailBean, callerJobDef);
		
//				RootBeanDefinition callerJobDef = new RootBeanDefinition(MethodInvokingJobDetailFactoryBean.class);
//				callerJobDef.getPropertyValues().addPropertyValue("targetObject", activeAgentCallerDef);
//				callerJobDef.getPropertyValues().addPropertyValue("targetMethod", "execute");
						
		// cron trigger per agent
		RootBeanDefinition cronTriggerDef = new RootBeanDefinition(CronTriggerBean.class);
		cronTriggerDef.getPropertyValues().addPropertyValue("jobDetail", callerJobDef);
		String cronExpression = parsePropertyString(agentElement, parserContext, CRON_EXPRESSION);
		cronTriggerDef.getPropertyValues().addPropertyValue(CRON_EXPRESSION, cronExpression);
		parserContext.getRegistry().registerBeanDefinition(cronTriggerBean, cronTriggerDef);
		
		// add a trigger definition to the main scheduler triggers list
		PropertyValue triggersDef = parser.getActiveAgentSchedulerDef().getPropertyValues().getPropertyValue("triggers");
		ManagedList managedList;
		if (triggersDef == null) {
			managedList = new ManagedList();
			triggersDef = new PropertyValue("triggers", managedList); 
		} else {
			managedList = (ManagedList)triggersDef.getValue();
		}
		managedList.add(cronTriggerDef);
		
		// add triggers to scheduler
		parser.getActiveAgentSchedulerDef().getPropertyValues().addPropertyValue(triggersDef);

	}
	
}
