package org.allmon.client.controller.namespace;

public class AllconControllerNamespaceParserException extends RuntimeException {

	public final static String NOT_SUPPORTED_FUNCTIONALITY = "This functionality is not supported";
		
	public AllconControllerNamespaceParserException(String exceptionString) {
		super(exceptionString);
	}
	
}
