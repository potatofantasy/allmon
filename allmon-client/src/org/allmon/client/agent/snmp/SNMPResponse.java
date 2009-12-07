package org.allmon.client.agent.snmp;

public class SNMPResponse {
	private String value;
	private String oid;
	private String error;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
