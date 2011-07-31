package org.allmon.client.agent.namespace;

import org.allmon.client.agent.JmxServerAgent;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class JmxServerAgentBeanDefinitionParser extends AbstractActiveAgentBeanDefinitionParser {

	// required	
	private static final String LVM_NAMES_REGEXP = "lvmNamesRegexp";
	private static final String HOST = "hostName";
	private static final String PORT = "port";
	
	private static final String MBEANS_ATTRNAMES_REGEXP = "mbeansAttributesNamesRegexp";
	private static final String MBEANS_OBJECT_NAME = "mbeansObjectName";
	private static final String MBEANS_ATTRIBUTE_NAME = "mbeansAttributeName";
	private static final String MBEANS_ATTRIBUTE_KEY_NAME = "mbeansAttributeKeyName";
	
//	public JmxServerAgentBeanDefinitionParser(ActiveAgentBeanDefinitionParser parser, String tagName) {
//		super(parser, tagName);
//	}

	protected void parseSpecifics(Element agentElement, ParserContext parserContext) {
		RootBeanDefinition agentDef = parseActiveAgentContext(agentElement, parserContext, JmxServerAgent.class);
		
		// properties of agent
		String lvmNamesRegexp = parsePropertyString(agentElement, parserContext, LVM_NAMES_REGEXP, false, true);
		agentDef.getPropertyValues().addPropertyValue(LVM_NAMES_REGEXP, lvmNamesRegexp);
		
		String host = parsePropertyString(agentElement, parserContext, HOST, false, true);
		agentDef.getPropertyValues().addPropertyValue(HOST, host);
		String port = parsePropertyString(agentElement, parserContext, PORT, false, true);
		agentDef.getPropertyValues().addPropertyValue(PORT, port);
		
		String mbeansAttributesNamesRegexp = parsePropertyString(agentElement, parserContext, MBEANS_ATTRNAMES_REGEXP, false, true);
		agentDef.getPropertyValues().addPropertyValue(MBEANS_ATTRNAMES_REGEXP, mbeansAttributesNamesRegexp);
		String mbeansObjectName = parsePropertyString(agentElement, parserContext, MBEANS_OBJECT_NAME, false, true);
		agentDef.getPropertyValues().addPropertyValue(MBEANS_OBJECT_NAME, mbeansObjectName);
		String mbeansAttributeName = parsePropertyString(agentElement, parserContext, MBEANS_ATTRIBUTE_NAME, false, true);
		agentDef.getPropertyValues().addPropertyValue(MBEANS_ATTRIBUTE_NAME, mbeansAttributeName);
		String mbeansAttributeKeyName = parsePropertyString(agentElement, parserContext, MBEANS_ATTRIBUTE_KEY_NAME, true, true);
		agentDef.getPropertyValues().addPropertyValue(MBEANS_ATTRIBUTE_KEY_NAME, mbeansAttributeKeyName);
	
	}
	
}