package org.allmon.client.controller.rules;

public class Condition {
	
	private String resource;
	private String relation;
	private double value;
	
	public Condition(String resource, String relation, double value) {
		this.resource = resource==null?"":resource;
		this.relation = relation;
		this.value = value;
	}
	
	public String getResource() {
		return resource;
	}
	
	public String getRelation() {
		return relation;
	}
	
	public double getValue() {
		return value;
	}

	public boolean exceeds(State s) {
		if (resource.equals(s.getResource())) {
			if (relation.equals("<")) {
				return (value - s.getValue()) > 0;
			} else if (relation.equals(">")) {
				return (value - s.getValue()) < 0;
			}
			return false;
		}
		throw new RuntimeException("Not comparable condition");
	}
	
}