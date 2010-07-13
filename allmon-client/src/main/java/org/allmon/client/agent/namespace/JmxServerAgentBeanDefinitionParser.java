package org.allmon.client.agent.namespace;

import org.allmon.client.agent.HttpUrlCallAgent;
import org.allmon.client.agent.JmxServerAgent;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class JmxServerAgentBeanDefinitionParser extends AbstractActiveAgentBeanDefinitionParser {

	// required	
	private static final String LVM_NAMES_REGEXP = "lvmNamesRegexp";
	private static final String MBEANS_ATTRNAMES_REGEXP = "mbeansAttributesNamesRegexp";
		
	public JmxServerAgentBeanDefinitionParser(ActiveAgentBeanDefinitionParser parser, String tagName) {
		super(parser, tagName);
	}

	protected void parseSpecifics(Element agentElement, ParserContext parserContext) {
		RootBeanDefinition agentDef = parseActiveAgentContext(agentElement, parserContext, HttpUrlCallAgent.class);
		
		// properties of agent
		String lvmNamesRegexp = parsePropertyString(agentElement, parserContext, LVM_NAMES_REGEXP);
		agentDef.getPropertyValues().addPropertyValue(LVM_NAMES_REGEXP, lvmNamesRegexp);
		
		String mbeansAttributesNamesRegexp = parsePropertyString(agentElement, parserContext, MBEANS_ATTRNAMES_REGEXP);
		agentDef.getPropertyValues().addPropertyValue(MBEANS_ATTRNAMES_REGEXP, mbeansAttributesNamesRegexp);
	}
	
}