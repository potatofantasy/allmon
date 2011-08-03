package org.allmon.client.controller.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * This class is allcon controller namespace handler.
 * 
 */
public class AllconControllerNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("controller", new ControllerBeanDefinitionParser());
	}

}