package org.allmon.client.agent.aop.ns;


public class HelloWorldImpl {
    
	private String message;
	
	private boolean silentMode = false;

	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void printMessage() {
    	if (!silentMode) {
    		System.out.println(message);
    	}
    }

    public void printMessage(String param) {
    	if (!silentMode) {
        	System.out.println(message + ":" + param);
    	}
    }
    
    public void printMessage(String [] param) {
    	if (!silentMode) {
        	System.out.println(message + ":" + param);
    	}
    }

    public void printMessageE() {
    	if (!silentMode) {
        	System.out.println(message);
    	}
    	throw new RuntimeException("An example exception!");
    }
    
    public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}
    
}
