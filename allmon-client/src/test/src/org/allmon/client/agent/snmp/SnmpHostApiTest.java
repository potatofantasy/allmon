package org.allmon.client.agent.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.snmp4j.mp.SnmpConstants;

public class SnmpHostApiTest {
	private static final String WINDOWS_HOST_IP = "192.168.200.129";
	private static final String LINUX_HOST_IP = "192.168.200.255";
	private static final String UNKNOWN_HOST_IP = "192.168.200.33";
	
	@Test
	public void testGetCpuLoadSnmp2c() {
		// Snmp ver. 2c is a default
		System.out.println("testGetCpuLoadSnmp2c() - Linux host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(LINUX_HOST_IP);

		SnmpHostApi api = new SnmpHostApi(settings);
		List<String> cpuLoadList = api.getCpuLoad();

		assertNotNull("result is null", cpuLoadList);
		assertEquals("Linux host has 2 processors", 2, cpuLoadList.size());
		Iterator<String> it = cpuLoadList.iterator();
		
		System.out.println("Linux host has 2 processors");

		// check CPU1
		String cpuLoad1 = it.next();
		assertEquals("There should not be error while reading cpu load", -1, cpuLoad1.indexOf("Error"));
		System.out.println("CPU1 load = " + cpuLoad1);
		
		// check CPU2
		String cpuLoad2 = it.next();
		assertEquals("There should not be error while reading cpu load", -1, cpuLoad2.indexOf("Error"));
		System.out.println("CPU2 load = " + cpuLoad2);
	}
	
	@Test
	public void testGetCpuLoadSnmp1() {
		System.out.println("testGetCpuLoadSnmp1() - Windows host");
		SnmpSettings settings = new SnmpSettings();
		settings.setSnmpVersion(SnmpConstants.version1);
		settings.setIPAddress(WINDOWS_HOST_IP);

		SnmpHostApi api = new SnmpHostApi(settings);
		List<String> cpuLoadList = api.getCpuLoad();

		assertNotNull("result is null", cpuLoadList);
		assertEquals("Windows host has 2 processors", 2, cpuLoadList.size());
		Iterator<String> it = cpuLoadList.iterator();
		
		System.out.println("Windows host has 2 processors");

		// check CPU1
		String cpuLoad1 = it.next();
		assertEquals("There should not be error while reading cpu load", -1, cpuLoad1.indexOf("Error"));
		System.out.println("CPU1 load = " + cpuLoad1);
		
		// check CPU2
		String cpuLoad2 = it.next();
		assertEquals("There should not be error while reading cpu load", -1, cpuLoad2.indexOf("Error"));
		System.out.println("CPU2 load = " + cpuLoad2);
	}	
	
	@Test
	public void testGetCpuLoadTimeout() {
		System.out.println("testGetCpuLoadTimeout() - Unknown host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(UNKNOWN_HOST_IP);

		SnmpHostApi api = new SnmpHostApi(settings);
		List<String> cpuLoadList = api.getCpuLoad();

		assertNotNull("result is null", cpuLoadList);
		assertEquals("Should be 1 error result", 1, cpuLoadList.size());
		Iterator<String> it = cpuLoadList.iterator();
		
		// check response
		String cpuLoad1 = it.next();
		assertEquals("There should timeout error while reading cpu load", 0, cpuLoad1.indexOf("Error"));
		System.out.println("Result = " + cpuLoad1);
	}	
	
	@Test
	public void testGetProcessList() {
		System.out.println("testGetProcessList() - Windows host");
		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(WINDOWS_HOST_IP);

		SnmpHostApi api = new SnmpHostApi(settings);
		List<ProcessPerfData> procTable = api.getProcessList();
		assertNotNull("result is null", procTable);

	}	
}
