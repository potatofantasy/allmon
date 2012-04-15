package org.allmon.client.controller.rules;


public class State {
	private String resource;
	private double value;
	
//	TODO refactor to this...:
//	public State(Resource resource, double value) {
//		this.resource = resource;
//		this.value = value;
//	}
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

	public String toString() {
		return resource + "=" + value;
	}
}
