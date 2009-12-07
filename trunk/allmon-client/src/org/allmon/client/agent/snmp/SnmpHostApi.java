package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.List;

public class SnmpHostApi {
	private SnmpSettings settings;
	public SnmpHostApi(SnmpSettings settings) {
		this.settings = settings;
	}	
	
	/**
	 * Reads cpu load from the SNMP managed host
	 * @return cpu load in [%] for each processor 
	 */
	public List<Integer> getCPULoad() {
        SnmpResponder res = new SnmpResponder(settings);	
        String[] columns = new String[1];
        columns[0] = HostResourcesMib.HR_PROCESSOR_LOAD_OID;
        List<SnmpResponseRow> rows = res.getTable(columns);
        
        List<Integer> cpuLoad= new ArrayList<Integer>();
        for (SnmpResponseRow snmpResponseRow : rows) {
        	List<SnmpResponse> rowList = snmpResponseRow.getRow();
        	for (SnmpResponse snmpResponse : rowList) {
        		cpuLoad.add(new Integer(snmpResponse.getValue()));
			}
		}
        return cpuLoad;
	}
}
