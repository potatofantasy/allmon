package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.List;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

public class SNMPExtractor {

	public SNMPResponse getResponse(ResponseEvent response) {
		PDU responsePDU = null;
		SNMPResponse snmpResponse = null;
		if (response != null) {
			responsePDU = response.getResponse();
		}

		if (responsePDU == null) {
			// request timed out
			snmpResponse = new SNMPResponse();
			snmpResponse.setError("Time out");
		}
		else {
			if(responsePDU.getErrorStatus() != 0)
			{
				snmpResponse = new SNMPResponse();
				snmpResponse.setError("SNMP4j error code: " + responsePDU.getErrorStatus());
			}
			else
			{
				// OK - received
				if (responsePDU.getVariableBindings() != null && !responsePDU.getVariableBindings().isEmpty()) {
					// extract the response
					snmpResponse = extractData(responsePDU.getVariableBindings().firstElement().toString());
				}
			}			
		}
		return snmpResponse;
	}

	public List<SNMPResponseRow> getResponseTable(List<TableEvent> listOfTableEvents) {
		List<SNMPResponseRow> responseTable = new ArrayList<SNMPResponseRow>();
		if (listOfTableEvents != null) {
			// each instance represents successfully retrieved row or an error condition
			for (TableEvent tableEvent : listOfTableEvents) {
				if(tableEvent.getStatus() != 0)
				{
					// error for the row
					SNMPResponseRow snmpResponseRow = new SNMPResponseRow();
					snmpResponseRow.setError("Error for the row: " + tableEvent.getErrorMessage());
					responseTable.add(snmpResponseRow);
				}
				else
				{
					// row OK					
					VariableBinding[] vbColumns = tableEvent.getColumns();
					// output row
					SNMPResponseRow snmpResponseRow = new SNMPResponseRow();
					responseTable.add(snmpResponseRow);
					// list of columns in the row
					List<SNMPResponse> rowList = new ArrayList<SNMPResponse>();
					snmpResponseRow.setRow(rowList);
					for (int i = 0; i < vbColumns.length; i++) {
						rowList.add(extractData(vbColumns[i].toString()));
					}					
				}
			}
		}	
		return responseTable;
	}

	protected SNMPResponse extractData(String responseStr) {
		SNMPResponse response = new SNMPResponse();
		if (responseStr.contains("=")) {
			int equalIndex = responseStr.indexOf("=");
			response.setValue(responseStr.substring(equalIndex + 2));
			response.setOid(responseStr.substring(0, equalIndex - 1));
		}		
		else {
			response.setError("Extractor error");
		}
		return response;
	}
}
