package org.allmon.client.agent.namespace;

public final class ActiveAgentBeanDefinitionParserFactory implements AllmonAgentBeanDefinitionParserFactory {

	static final String HTTP_URL_CALL_AGENT = "httpUrlCallAgent";
	static final String JMX_SERVER_AGENT = "jmxServerAgent";
		
	public AgentBeanDefinitionElementParser getParser(String localName) {
		AbstractActiveAgentBeanDefinitionParser parser = null;
		if (HTTP_URL_CALL_AGENT.equals(localName)) {
			parser = new HttpUrlCallAgentBeanDefinitionParser();
		} else if (JMX_SERVER_AGENT.equals(localName)) {
			parser = new JmxServerAgentBeanDefinitionParser();
		} else {
			throw new AllmonNamespaceParserException(NOT_SUPPORTED_FUNCTIONALITY);
		}
		parser.setTagName(localName);
		return parser;
	}
	
}
