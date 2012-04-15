package org.allmon.client.controller.advices;

public class ControllerException extends Exception {

	private String message;
	
	public ControllerException(String message) {
		this.message = message;
	}
	
}
