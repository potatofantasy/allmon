package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.List;

public class SnmpErrors {
	public static final String TIME_OUT = "Time out";
	public static final String RESPONSE_ERR_CODE = "Response err code: ";
	public static final String EXTRACTOR = "Extractor error";
	public static final String CANT_LISTEN_PORT = "Can't listen on SNMP port";
	
	
	protected static SnmpResponse getErrorResponse(String msg) {
		SnmpResponse response = new SnmpResponse();
		response.setError(msg);
		return response;
	}

	protected static SnmpResponseRow getErrorResponseRow(String msg) {
		SnmpResponseRow responseRow = new SnmpResponseRow();
		List<SnmpResponse> responseRowList = new ArrayList<SnmpResponse>();
		responseRowList.add(getErrorResponse(msg));
		responseRow.setRow(responseRowList);
		return responseRow;
	}
	
	protected static List<SnmpResponseRow> getErrorResponseRowList(String msg) {
		List<SnmpResponseRow> l = new ArrayList<SnmpResponseRow>();
		l.add(getErrorResponseRow(msg));
		return l;
	}	
}
