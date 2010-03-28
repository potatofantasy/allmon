package org.allmon.client.agent.snmp;

import java.util.List;

public class SnmpResponseRow {
	private List<SnmpResponse> row;
	private String error;
	public List<SnmpResponse> getRow() {
		return row;
	}
	public void setRow(List<SnmpResponse> row) {
		this.row = row;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
