package org.allmon.client.agent.jmx.spring;

public class JmxTestBean {

	private String name;

	private int age;

	private boolean isSuperman;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int add(int x, int y) {
		return x + y;
	}

	public void dontExposeMe() {
		throw new RuntimeException();
	}
	
	public boolean randomAge() {
		age = (int) (Math.random() * 100);
		return true;
	}

	public boolean isSuperman() {
		return isSuperman;
	}

	public void setSuperman(boolean isSuperman) {
		this.isSuperman = isSuperman;
	}
	
    public String toString() {
    	return "Name:" + getName() + " ,Age:" + getAge() + " ,is superman:" + isSuperman();
    }

    
}