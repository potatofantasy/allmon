package org.allmon.client.agent.snmp;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class SnmpResponderTest extends TestCase {

    @Test
    public void testGetOid() {
        try {
            SnmpSettings settings = new SnmpSettings();
            settings.setIPAddress("192.168.200.130");

            SnmpResponder res = new SnmpResponder(settings);
            String cpuId = ".768";
            SnmpResponse cpuLoad = res.getNext(HostResourcesMib.HR_PROCESSOR_LOAD_OID + cpuId);
            System.out.println("CPU load: " + cpuLoad.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSnmpGetTable() {
        SnmpSettings settings = new SnmpSettings();
        settings.setIPAddress("localhost"); // 192.168.200.130");

        SnmpResponder res = new SnmpResponder(settings);
        String[] columns = new String[1];
        columns[0] = HostResourcesMib.HR_PROCESSOR_LOAD_OID;
        List<SnmpResponseRow> rows = res.getTable(columns);

        for (SnmpResponseRow snmpResponseRow : rows) {
            List<SnmpResponse> rowList = snmpResponseRow.getRow();
            for (SnmpResponse snmpResponse : rowList) {
                System.out.println("oid = " + snmpResponse.getOid() + " cpu load = " + snmpResponse.getValue());
            }
        }

    }
}
