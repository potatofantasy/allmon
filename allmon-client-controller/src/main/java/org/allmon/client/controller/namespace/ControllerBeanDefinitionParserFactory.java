package org.allmon.client.controller.namespace;

public class ControllerBeanDefinitionParserFactory {

	private static final String JAVA_CALL_TERMINATOR = "javaCallTerminator";
	
	public AbstractControllerBeanDefinitionParser getParser(String localName) {
		AbstractControllerBeanDefinitionParser parser = null;
		if (JAVA_CALL_TERMINATOR.equals(localName)) {
			parser = new JavaCallTerminatorBeanDefinitionParser();
		} else {
			throw new AllconControllerNamespaceParserException(
					AllconControllerNamespaceParserException.NOT_SUPPORTED_FUNCTIONALITY);
		}
		parser.setTagName(localName);
		return parser;
	}
	
}
