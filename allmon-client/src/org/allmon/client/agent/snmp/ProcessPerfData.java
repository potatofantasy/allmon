package org.allmon.client.agent.snmp;

public class ProcessPerfData {
	private int id;
	private String name;
	private SnmpSwRunType type;
	private int cpuTime;
	private int memory;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SnmpSwRunType getType() {
		return type;
	}
	public void setType(SnmpSwRunType type) {
		this.type = type;
	}
	public int getCpuTime() {
		return cpuTime;
	}
	public void setCpuTime(int cpuTime) {
		this.cpuTime = cpuTime;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
}
