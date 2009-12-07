package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.List;

public class SNMPHostAPI {
	private SNMPSettings settings;
	public SNMPHostAPI(SNMPSettings settings) {
		this.settings = settings;
	}	
	
	/**
	 * Reads cpu load from the SNMP managed host
	 * @return cpu load in [%] for each processor 
	 */
	public List<Integer> getCPULoad() {
        SNMPResponder res = new SNMPResponder(settings);	
        String[] columns = new String[1];
        columns[0] = HostResourcesMIB.HR_PROCESSOR_LOAD_OID;
        List<SNMPResponseRow> rows = res.getTable(columns);
        
        List<Integer> cpuLoad= new ArrayList<Integer>();
        for (SNMPResponseRow snmpResponseRow : rows) {
        	List<SNMPResponse> rowList = snmpResponseRow.getRow();
        	for (SNMPResponse snmpResponse : rowList) {
        		cpuLoad.add(new Integer(snmpResponse.getValue()));
			}
		}
        return cpuLoad;
	}
}
