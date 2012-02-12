package org.allmon.client.controller.rules;

public class State {
	private String resource;
	private double value;
	
	public State(String resource, double value) {
		this.resource = resource;
		this.value = value;
	}
	
	public String getResource() {
		return resource;
	}
	
	public double getValue() {
		return value;
	}
	
}
