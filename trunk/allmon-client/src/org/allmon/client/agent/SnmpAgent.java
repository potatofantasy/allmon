package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.snmp.SnmpHostApi;
import org.allmon.client.agent.snmp.SnmpSettings;
import org.allmon.common.MetricMessageWrapper;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpAgent extends ActiveAgent {

    public SnmpAgent(AgentContext agentContext) {
		super(agentContext);
	}

	MetricMessageWrapper collectMetrics() {
        // TODO add collecting snmp metrics code
        SnmpSettings settings = new SnmpSettings();
        settings.setIPAddress("192.168.200.130");
        SnmpHostApi snmpHostAPI = new SnmpHostApi(settings);
        List<String> cpuLoad = snmpHostAPI.getCpuLoad();
        
        // TODO: send metrics...
        return null;
    }

    protected void decodeAgentTaskableParams() {
    }
    


}
