package org.allmon.client.agent.snmp;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class SnmpResponderTest extends TestCase {
	private static final String WINDOWS_HOST_IP = "192.168.200.129";
	private static final String LINUX_HOST_IP = "192.168.200.255";
	private static final String UNKNOWN_HOST_IP = "192.168.200.33";
	
	@Test
	public void testGetNext() {
		System.out.println("testGetNext() - Windows host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(WINDOWS_HOST_IP);

		SnmpResponder res = new SnmpResponder(settings);
		String cpuId = ".768";
		SnmpResponse cpuLoad = res
				.getNext(HostResourcesMib.HR_PROCESSOR_LOAD_OID + cpuId);
		if (cpuLoad.getError() == null) {
			System.out.println("CPU load: " + cpuLoad.getValue());
		} else {
			System.out.println("Error: " + cpuLoad.getError());
			fail("Provide correct host IP");
		}
	}
	
	@Test
	public void testGetNextTimeout() {
		System.out.println("testGetNext() - Unknown host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(UNKNOWN_HOST_IP);

		SnmpResponder res = new SnmpResponder(settings);
		String cpuId = ".768";
		SnmpResponse cpuLoad = res
				.getNext(HostResourcesMib.HR_PROCESSOR_LOAD_OID + cpuId);
		if (cpuLoad.getError() == null) {
			System.out.println("CPU load: " + cpuLoad.getValue());
			fail("Unknown host should not be SNMP enabled");			
		} else {
			System.out.println("Expected error: " + cpuLoad.getError());
		}
	}	

	@Test
	public void testSnmpGetTable() {
		System.out.println("testSnmpGetTable() - Windows host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(WINDOWS_HOST_IP);

		SnmpResponder res = new SnmpResponder(settings);
		String[] columns = new String[1];
		columns[0] = HostResourcesMib.HR_PROCESSOR_LOAD_OID;
		List<SnmpResponseRow> rows = res.getTable(columns);

		for (SnmpResponseRow snmpResponseRow : rows) {
			List<SnmpResponse> rowList = snmpResponseRow.getRow();
			for (SnmpResponse snmpResponse : rowList) {
				if (snmpResponse.getError() == null) {
					System.out.println("oid = " + snmpResponse.getOid()
							+ " cpu load = " + snmpResponse.getValue());
				} else {
					System.out.println("Error: " + snmpResponse.getError());
					fail("Provide correct host IP");
				}
			}
		}
	}

	@Test
	public void testSnmpGetColumn() {
		System.out.println("testSnmpGetColumn() - Linux host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(LINUX_HOST_IP);

		SnmpResponder res = new SnmpResponder(settings);
		SnmpResponseRow responseColumn = res
				.getColumn(HostResourcesMib.HR_PROCESSOR_LOAD_OID);

		List<SnmpResponse> rowList = responseColumn.getRow();
		for (SnmpResponse snmpResponse : rowList) {
			if (snmpResponse.getError() == null) {
				System.out.println("oid = " + snmpResponse.getOid()
						+ " cpu load = " + snmpResponse.getValue());
			} else {
				System.out.println("Error: " + snmpResponse.getError());
				fail("Provide correct host IP");
			}
		}
	}

}
