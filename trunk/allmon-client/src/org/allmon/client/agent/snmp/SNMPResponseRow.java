package org.allmon.client.agent.snmp;

import java.util.List;

public class SNMPResponseRow {
	private List<SNMPResponse> row;
	private String error;
	public List<SNMPResponse> getRow() {
		return row;
	}
	public void setRow(List<SNMPResponse> row) {
		this.row = row;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
