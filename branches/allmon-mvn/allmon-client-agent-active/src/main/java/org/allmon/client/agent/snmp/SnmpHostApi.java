package org.allmon.client.agent.snmp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SnmpHostApi {
	public static final String ERROR_STR = "Error: ";
	private SnmpSettings settings;

	public SnmpHostApi(SnmpSettings settings) {
		this.settings = settings;
	}

	/**
	 * Reads cpu load from the SNMP managed host. The output is [%] of usage in
	 * last 1 minute. There is no point in calling this agent more than
	 * once/minute. If error the result contains error string which starts with
	 * 'Error:' This implementation uses getColumn which is faster than
	 * getTable.
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

	public List<ProcessPerfData> getProcessList() {
		SnmpResponder responder = new SnmpResponder(settings);
		String[] oids = { HostResourcesMib.HR_SW_RUN_INDEX,
				HostResourcesMib.HR_SW_RUN_NAME,
				HostResourcesMib.HR_SW_RUN_TYPE,
				HostResourcesMib.HR_SW_RUN_PERF_CPU,
				HostResourcesMib.HR_SW_RUN_PERF_MEM };

		List<SnmpResponseRow> processTable = responder.getTable(oids);
		
		List<ProcessPerfData> processList = new ArrayList<ProcessPerfData>();
		for (SnmpResponseRow snmpResponseRow : processTable) {
			if (snmpResponseRow.getRow().size() == oids.length) {
				ProcessPerfData p = new ProcessPerfData();
				Iterator<SnmpResponse> it = snmpResponseRow.getRow().iterator();
				// set process data
				try {
				p.setId(Integer.parseInt(it.next().getValue()));
				p.setName(it.next().getValue());
				SnmpSwRunType type = SnmpSwRunType.getEnum(Integer.parseInt(it.next().getValue()));
				p.setType(type);
				p.setCpuTime(Integer.parseInt(it.next().getValue()));
				p.setMemory(Integer.parseInt(it.next().getValue()));
				processList.add(p);
				}
				catch(NumberFormatException e) {
					// skip
				}
			}
			// else skip this process, do not return error
		}
		return processList;
	}

}
