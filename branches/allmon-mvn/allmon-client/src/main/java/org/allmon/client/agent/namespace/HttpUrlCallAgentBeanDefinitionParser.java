package org.allmon.client.agent.namespace;

import org.allmon.client.agent.ActiveAgentCaller;
import org.allmon.client.agent.HttpUrlCallAgent;
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

public class HttpUrlCallAgentBeanDefinitionParser extends AbstractActiveAgentBeanDefinitionParser {

	private static final String URL_ADDRESS = "urlAddress";
	private static final String CRON_EXPRESSION = "cronExpression";
	
	public HttpUrlCallAgentBeanDefinitionParser(ActiveAgentBeanDefinitionParser parser) {
		super(parser);
	}

	protected void parseSpecifics(Element agentElement, ParserContext parserContext) {
		//AbstractBeanDefinition advisorDef = createHttpUrlCallAgentBeanDefinition(agentElement, parserContext);
		RootBeanDefinition agentDef = new RootBeanDefinition(HttpUrlCallAgent.class);
		agentDef.setSource(parserContext.extractSource(agentElement));
		
		String agentBeanName = agentElement.getAttribute(ID);
		if (StringUtils.hasText(agentBeanName)) {
			parserContext.getRegistry().registerBeanDefinition(agentBeanName, agentDef);
		} else {
			agentBeanName = parserContext.getReaderContext().registerWithGeneratedName(agentDef);
		}

		Object agentContext = parseAgentContextProperty(agentElement, parserContext);
		if (agentContext instanceof BeanDefinition) {
			//agentDef.getPropertyValues().addPropertyValue(AGENT_CONTEXT_REF, agentContext);
			agentDef.getConstructorArgumentValues().addGenericArgumentValue(agentContext);
		}
		else if (agentContext instanceof String) {
			//agentDef.getPropertyValues().addPropertyValue(AGENT_CONTEXT_REF, new RuntimeBeanReference((String) agentContext));
			agentDef.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference((String)agentContext));
		}
		
		// agent caller
//				RootBeanDefinition activeAgentCallerDef = new RootBeanDefinition(ActiveAgentCaller.class);
//				activeAgentCallerDef.getPropertyValues().addPropertyValue("activeAgent", agentDef);
		
		// job
		ManagedMap managedMap = new ManagedMap();
		managedMap.put("activeAgent", agentDef);
		RootBeanDefinition callerJobDef = new RootBeanDefinition(JobDetailBean.class);
		callerJobDef.getPropertyValues().addPropertyValue("jobClass", ActiveAgentCaller.class.getName());
		callerJobDef.getPropertyValues().addPropertyValue("jobDataAsMap", managedMap);
		
//				RootBeanDefinition callerJobDef = new RootBeanDefinition(MethodInvokingJobDetailFactoryBean.class);
//				callerJobDef.getPropertyValues().addPropertyValue("targetObject", activeAgentCallerDef);
//				callerJobDef.getPropertyValues().addPropertyValue("targetMethod", "execute");
						
		// cron trigger per agent
		RootBeanDefinition cronTriggerDef = new RootBeanDefinition(CronTriggerBean.class);
		cronTriggerDef.getPropertyValues().addPropertyValue("jobDetail", callerJobDef);
		String cronExpression = parsePropertyString(agentElement, parserContext, CRON_EXPRESSION);
		cronTriggerDef.getPropertyValues().addPropertyValue(CRON_EXPRESSION, cronExpression);
		
		// triggers (list)
		ManagedList managedList = new ManagedList();
		managedList.add(cronTriggerDef);
		PropertyValue triggersDef = new PropertyValue("triggers", managedList); 
		
		// adding triggers to scheduler
		//parserContext.getRegistry().getBeanDefinition((String)"agentScheduler").
		//	getPropertyValues().addPropertyValue(triggersDef);
		parser.getActiveAgentScheduletDef().getPropertyValues().addPropertyValue(triggersDef);
		
		
		// properties of agent
		String url = parsePropertyString(agentElement, parserContext, URL_ADDRESS);
		agentDef.getPropertyValues().addPropertyValue(URL_ADDRESS, url);

	}
	
}
