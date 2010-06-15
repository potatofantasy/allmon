package org.allmon.client.agent.namespace;

import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public abstract class AbstractActiveAgentBeanDefinitionParser {

	protected static final String ID = "id";
	
	private static final String AGENT_CONTEXT_REF = "agentContextRef";
	
	protected final ActiveAgentBeanDefinitionParser parser;
	
	public AbstractActiveAgentBeanDefinitionParser(ActiveAgentBeanDefinitionParser parser) {
		this.parser = parser;
	}
	
	protected abstract void parseSpecifics(Element agentElement, ParserContext parserContext);
	
	public final void parse(Element agentElement, ParserContext parserContext) {
		String id = agentElement.getAttribute(ID);
		try {
			parser.getParseState().push(new AgentEntry(id));
			parseSpecifics(agentElement, parserContext);
		} finally {
			parser.getParseState().pop();
		}
	}

	protected final Object parseAgentContextProperty(Element element, ParserContext parserContext) {
//		if (element.hasAttribute(AGENT_CONTEXT) && element.hasAttribute(AGENT_CONTEXT_REF)) {
//			parserContext.getReaderContext().error(
//					"Cannot define both 'agentContext' and 'agentContextRef' on <httpUrlCallAgent> tag.",
//					element, this.parseState.snapshot());
//			return null;
//		} else 
//		if (element.hasAttribute(AGENT_CONTEXT)) {
//			String expression = element.getAttribute(AGENT_CONTEXT);
//			AbstractBeanDefinition definition = createPointcutDefinition(expression);
//			pointcutDefinition.setSource(parserContext.extractSource(element));
//			return definition;
//		}
//		else if (element.hasAttribute(AGENT_CONTEXT_REF)) {
//			String agentContextRef = element.getAttribute(AGENT_CONTEXT_REF);
//			if (!StringUtils.hasText(agentContextRef)) {
//				parserContext.getReaderContext().error(
//						"'agentContextRef' attribute contains empty value.", element, this.parseState.snapshot());
//				return null;
//			}
//			return agentContextRef;
//		}
		if (element.hasAttribute(AGENT_CONTEXT_REF)) {
			String agentContextRef = element.getAttribute(AGENT_CONTEXT_REF);
			if (!StringUtils.hasText(agentContextRef)) {
				parserContext.getReaderContext().error(
						"'agentContextRef' attribute contains empty value.", element, parser.getParseState().snapshot());
				return null;
			}
			return agentContextRef;
		}
		else {
			parserContext.getReaderContext().error(
					"Must define one of 'agentContext' <httpUrlCallAgent> tag.",
					element, parser.getParseState().snapshot());
			return null;
		}
	}
	
	protected final String parsePropertyString(Element element, ParserContext parserContext, String attribute) {
		if (element.hasAttribute(attribute)) {
			String property = element.getAttribute(attribute);
			if (!StringUtils.hasText(property)) {
				parserContext.getReaderContext().error(
						"'"+attribute+"' attribute contains empty value.", element, parser.getParseState().snapshot());
				return null;
			}
			return property;
		}
		else {
			parserContext.getReaderContext().error(
					"Must define one of '"+attribute+"' <httpUrlCallAgent> tag.",
					element, parser.getParseState().snapshot());
			return null;
		}
	}
	
}
