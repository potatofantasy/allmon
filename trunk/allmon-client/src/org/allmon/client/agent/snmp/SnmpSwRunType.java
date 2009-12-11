package org.allmon.client.agent.snmp;

public enum SnmpSwRunType {
	unknown(1), operatingSystem(2), deviceDriver(3), application(4);
	
	private static final String[] types = {"unknown"/* (0) */,
			"unknown"/* (1) */, "operatingSystem"/* (2) */,
			"deviceDriver"/* (3) */, "application"/* (4) */};

	private int code;

	private SnmpSwRunType(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static SnmpSwRunType getEnum(int i) {
		if (i < types.length)
			return valueOf(types[i]);
		else
			return unknown;
	}
}
