package org.allmon.client.agent.aop.basic.controller;

import java.util.ArrayList;

public class HelloWorldImpl implements HelloWorldInterface {
    
	private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void printMessage() {
    	ArrayList<String> arrayList = new ArrayList<String>();
    	
    	for(int i=0; i< 100; i++)
    	{
    		arrayList.add(new String("" + i));
    	}
    	System.out.println(message);
    }

}
