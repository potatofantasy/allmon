package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.List;

public class SnmpHostApi {
	public static final String ERROR_STR = "Error: ";
	private SnmpSettings settings;
	public SnmpHostApi(SnmpSettings settings) {
		this.settings = settings;
	}	
	
	/**
	 * Reads cpu load from the SNMP managed host. The output is [%] of usage in last 1 minute.
	 * There is no point in calling this agent more than once/minute.
	 * If error the result contains error string which starts with 'Error:'
	 * This implementation uses getColumn which is faster than getTable.
	 * 
	 * @return cpu load in [%] for each processor 
	 */
	public List<String> getCpuLoad() {
        SnmpResponder responder = new SnmpResponder(settings);	       
		SnmpResponseRow responseColumn = responder
				.getColumn(HostResourcesMib.HR_PROCESSOR_LOAD_OID);

		List<String> result = new ArrayList<String>();
		List<SnmpResponse> rowList = responseColumn.getRow();
		for (SnmpResponse snmpResponse : rowList) {
			if (snmpResponse.getError() == null) {
				result.add(snmpResponse.getValue());
			} else {
				result.add(ERROR_STR + snmpResponse.getError());
			}
		}
        return result;
	}
}
