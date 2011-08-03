package org.allmon.client.controller.aop.ns;

public class HelloWorldImpl {
    
	private String message;
	
	private boolean silentMode = false;

	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String printMessage() {
    	if (!silentMode) {
    		System.out.println(message);
    	}
    	return message;
    }

    public String printMessage(String param) {
    	String paramMessage = message + ":" + param;
    	if (!silentMode) {
        	System.out.println(paramMessage);
    	}
    	return paramMessage;
    }
    
    public void printMessage(long delay) {
    	if (!silentMode) {
        	System.out.println(message + " with delay:" + delay);
        	try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}    	
    }
    
    public String printMessage(String [] param) {
    	String paramMessage = message + ":" + param;
    	if (!silentMode) {
        	System.out.println(message + ":" + param);
    	}
    	return paramMessage;
    }

    public String printMessage(String param1, String param2) {
    	String paramMessage = message + ":" + param1 + param2;
    	if (!silentMode) {
        	System.out.println(paramMessage);
    	}
    	return paramMessage;
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
