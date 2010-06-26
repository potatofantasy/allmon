package org.allmon.client.agent.aop.ns;


public class HelloWorldImpl {
    
	private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void printMessage() {
    	System.out.println(message);
    }

    public void printMessage(String param) {
    	System.out.println(message + ":" + param);
    }
    
    public void printMessage(String [] param) {
    	System.out.println(message + ":" + param);
    }

    public void printMessageE() {
    	System.out.println(message);
    	throw new RuntimeException("An example exception!");
    }
    
}
