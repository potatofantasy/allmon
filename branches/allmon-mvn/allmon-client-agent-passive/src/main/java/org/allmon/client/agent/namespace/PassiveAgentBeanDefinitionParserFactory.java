package org.allmon.client.agent.namespace;

public class PassiveAgentBeanDefinitionParserFactory implements AllmonAgentBeanDefinitionParserFactory {

	private static final String JAVA_CALL_AGENT = "javaCallAgent";
	private static final String ACTION_CLASS_AGENT = "actionClassAgent";
	private static final String SERVLET_CALL_AGENT = "servletCallAgent";
	
	public AgentBeanDefinitionElementParser getParser(String localName) {
		AbstractPassiveAgentBeanDefinitionParser parser = null;
		if (JAVA_CALL_AGENT.equals(localName)) {
			parser = new JavaCallAgentBeanDefinitionParser();
		} else if (ACTION_CLASS_AGENT.equals(localName)) {
			throw new AllmonNamespaceParserException(NOT_SUPPORTED_FUNCTIONALITY + " yet");
		} else if (SERVLET_CALL_AGENT.equals(localName)) {
			throw new AllmonNamespaceParserException(NOT_SUPPORTED_FUNCTIONALITY + " yet");
		} else {
			throw new AllmonNamespaceParserException(NOT_SUPPORTED_FUNCTIONALITY);
		}
		parser.setTagName(localName);
		return parser;
	}
	
}
