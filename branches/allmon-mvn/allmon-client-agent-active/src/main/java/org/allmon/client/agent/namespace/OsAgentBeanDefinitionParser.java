package org.allmon.client.agent.namespace;

import org.allmon.client.agent.OsAgent;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class OsAgentBeanDefinitionParser extends AbstractActiveAgentBeanDefinitionParser {

	// required	
	private static final String METRIC_TYPE = "metricType";

	protected void parseSpecifics(Element agentElement, ParserContext parserContext) {
		RootBeanDefinition agentDef = parseActiveAgentContext(agentElement, parserContext, OsAgent.class);
		
		// properties of agent
		String metricTypeString = parsePropertyString(agentElement, parserContext, METRIC_TYPE, false, true);
		agentDef.getPropertyValues().addPropertyValue(METRIC_TYPE, metricTypeString);
	}
	
}