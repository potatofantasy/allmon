package org.allmon.client.agent.aop;

public class SpringHelloWorld implements HelloWorldInterface {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void printMessage()
	{
		System.out.println(message);
	}

}
