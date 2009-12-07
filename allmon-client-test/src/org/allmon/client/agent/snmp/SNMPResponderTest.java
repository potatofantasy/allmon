package org.allmon.client.agent.snmp;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class SNMPResponderTest extends TestCase{
	@Test
    public void testGetOid() {
        try {
            SNMPSettings settings = new SNMPSettings();
            settings.setIPAddress("192.168.200.130");
            
            SNMPResponder res = new SNMPResponder(settings);
            String cpuId = ".768";
            SNMPResponse cpuLoad = res.getNext(HostResourcesMIB.HR_PROCESSOR_LOAD_OID + cpuId);
            System.out.println("CPU load: " + cpuLoad.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Test
    public void testSnmpGetTable() {
        SNMPSettings settings = new SNMPSettings();
        settings.setIPAddress("192.168.200.130");
        
        SNMPResponder res = new SNMPResponder(settings);	
        String[] columns = new String[1];
        columns[0] = HostResourcesMIB.HR_PROCESSOR_LOAD_OID;
        List<SNMPResponseRow> rows = res.getTable(columns);
        
        for (SNMPResponseRow snmpResponseRow : rows) {
        	List<SNMPResponse> rowList = snmpResponseRow.getRow();
        	for (SNMPResponse snmpResponse : rowList) {
				System.out.println("oid = " + snmpResponse.getOid() + " cpu load = " + snmpResponse.getValue());
			}
		}
        
        
    }
}
