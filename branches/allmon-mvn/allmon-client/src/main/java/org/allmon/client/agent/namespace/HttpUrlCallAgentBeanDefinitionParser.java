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

	// required
	private static final String URL_ADDRESS = "urlAddress";
	
	private static final String SEARCH_PHRASE = "searchPhrase";
	private static final String CHECK_NAME = "checkName";
	private static final String INSTANCE_NAME = "instanceName";
	
	// optional
	private static final String STRATEGY = "strategy";
	private static final String CONTENT_TYPE = "contentType";
	private static final String URL_PARAMETERS = "urlParameters";
	private static final String CHECKING_HOST = "checkingHost";
	private static final String USE_PROXY = "useProxy";
	private static final String REQUEST_METHOD = "requestMethod";
	
	private static int instanceCounter = 0;
	
	public HttpUrlCallAgentBeanDefinitionParser(ActiveAgentBeanDefinitionParser parser, String tagName) {
		super(parser, tagName);
	}

	protected void parseSpecifics(Element agentElement, ParserContext parserContext) {
		RootBeanDefinition agentDef = parseActiveAgentContext(agentElement, parserContext, HttpUrlCallAgent.class);
				
		// properties of agent
		String url = parsePropertyString(agentElement, parserContext, URL_ADDRESS);
		agentDef.getPropertyValues().addPropertyValue(URL_ADDRESS, url);

		String searchPhrase = parsePropertyString(agentElement, parserContext, SEARCH_PHRASE);
		agentDef.getPropertyValues().addPropertyValue(SEARCH_PHRASE, searchPhrase);

		String checkName = parsePropertyString(agentElement, parserContext, CHECK_NAME);
		agentDef.getPropertyValues().addPropertyValue(CHECK_NAME, checkName);
		
		String instanceName = parsePropertyString(agentElement, parserContext, INSTANCE_NAME);
		agentDef.getPropertyValues().addPropertyValue(INSTANCE_NAME, instanceName);
		
		String strategy = parsePropertyString(agentElement, parserContext, STRATEGY, true);
		if (strategy != null) {
			agentDef.getPropertyValues().addPropertyValue("strategyClassName", strategy);
		}
		
		String contentType = parsePropertyString(agentElement, parserContext, CONTENT_TYPE, true);
		if (contentType != null) {
			agentDef.getPropertyValues().addPropertyValue(CONTENT_TYPE, contentType);
		}
		
		String urlParameters = parsePropertyString(agentElement, parserContext, URL_PARAMETERS, true);
		if (urlParameters != null) {
			agentDef.getPropertyValues().addPropertyValue(URL_PARAMETERS, urlParameters);
		}
		
		String checkingHost = parsePropertyString(agentElement, parserContext, CHECKING_HOST, true);
		if (checkingHost != null) {
			agentDef.getPropertyValues().addPropertyValue(CHECKING_HOST, checkingHost);
		}
		
		String useProxy = parsePropertyString(agentElement, parserContext, USE_PROXY, true);
		if (useProxy != null) {
			agentDef.getPropertyValues().addPropertyValue(USE_PROXY, useProxy);
		}

		String requestMethod = parsePropertyString(agentElement, parserContext, REQUEST_METHOD, true);
		if (requestMethod != null) {
			agentDef.getPropertyValues().addPropertyValue(REQUEST_METHOD, requestMethod);
		}
		
	}
	
}
