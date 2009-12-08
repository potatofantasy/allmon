package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.List;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

public class SnmpExtractor {

	public SnmpResponse getResponse(ResponseEvent response) {
		PDU responsePDU = null;
		SnmpResponse snmpResponse = null;
		if (response != null) {
			responsePDU = response.getResponse();
		}

		if (responsePDU == null) {
			// request timed out
			snmpResponse = new SnmpResponse();
			snmpResponse.setError("Time out");
		}
		else {
			if(responsePDU.getErrorStatus() != 0)
			{
				snmpResponse = new SnmpResponse();
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
	
	/**
	 * 
	 * @param listOfTableEvents
	 * @return
	 */
	public List<SnmpResponseRow> getResponseTable(List<TableEvent> listOfTableEvents) {
		List<SnmpResponseRow> responseTable = new ArrayList<SnmpResponseRow>();
		if (listOfTableEvents != null) {
			// each instance represents successfully retrieved row or an error condition
			for (TableEvent tableEvent : listOfTableEvents) {
				if(tableEvent.getStatus() != 0)
				{
					// error for the row
					SnmpResponseRow snmpResponseRow = new SnmpResponseRow();
					snmpResponseRow.setError(tableEvent.getErrorMessage());
					responseTable.add(snmpResponseRow);
				}
				else
				{
					// row OK					
					VariableBinding[] vbColumns = tableEvent.getColumns();
					// output row
					SnmpResponseRow snmpResponseRow = new SnmpResponseRow();
					responseTable.add(snmpResponseRow);
					// list of columns in the row
					List<SnmpResponse> rowList = new ArrayList<SnmpResponse>();
					snmpResponseRow.setRow(rowList);
					for (int i = 0; i < vbColumns.length; i++) {
						rowList.add(extractData(vbColumns[i].toString()));
					}					
				}
			}
		}	
		return responseTable;
	}
	
	/**
	 * Extracts 1st column from the response as SNMPResponseRow
	 * @param listOfTableEvents
	 * @return
	 */
	public SnmpResponseRow getResponseColumn(List<TableEvent> listOfTableEvents) {
		SnmpResponseRow responseRow = new SnmpResponseRow();
		List<SnmpResponse> colList = new ArrayList<SnmpResponse>();
		responseRow.setRow(colList);
		if (listOfTableEvents != null) {
			// each instance represents successfully retrieved row or an error condition
			for (TableEvent tableEvent : listOfTableEvents) {
				if(tableEvent.getStatus() != 0)
				{
					// error for the row
					SnmpResponse snmpResponse = new SnmpResponse();
					snmpResponse.setError(tableEvent.getErrorMessage());
					colList.add(snmpResponse);
				}
				else
				{
					// row OK					
					VariableBinding[] vbColumns = tableEvent.getColumns();
					// add 1st column only
					colList.add(extractData(vbColumns[0].toString()));
					// list of columns in the row
				}
			}
		}	
		return responseRow;
	}	

	protected SnmpResponse extractData(String responseStr) {
		SnmpResponse response = new SnmpResponse();
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
