package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.snmp.SnmpException;
import org.allmon.client.agent.snmp.SnmpHostApi;
import org.allmon.client.agent.snmp.SnmpSettings;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SnmpHostAgent extends ActiveAgent {
    private static final Log logger = LogFactory.getLog(SnmpHostAgent.class);
    private String managedHost;
    public SnmpHostAgent(AgentContext agentContext) {
		super(agentContext);
	}

    /**
     * WARNING: not tested!!!
     * TODO: There will be many metrics for the host,
     * Command design pattern should be considered to collect a selection of host metrics.
     * At the moment we only collect cpu load. 
     */
	MetricMessageWrapper collectMetrics() {
		logger.info("Collecting metrics for host: " + managedHost);
		MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
		
        SnmpSettings settings = new SnmpSettings();
        settings.setIPAddress(managedHost);
        SnmpHostApi snmpHostAPI = new SnmpHostApi(settings);
        List<String> cpuLoadList = snmpHostAPI.getCpuLoad();
        
        int cpuNum = 1;
        for (String cpuLoad : cpuLoadList) {
        	Exception snmpException = null;
        	double cpuLoadValue = 0;
			if (cpuLoad.indexOf(SnmpHostApi.ERROR_STR) != -1) {
				// no error string
				cpuLoadValue = new Double(cpuLoadValue);
			} else {
				snmpException = new SnmpException(cpuLoad.substring(SnmpHostApi.ERROR_STR.length()));
			}
	        MetricMessage metricMessage = MetricMessageFactory.createCpuLoadMessage(cpuNum++, cpuLoadValue, snmpException);
	        metricMessageWrapper.add(metricMessage);
		}
        
        return metricMessageWrapper;
    }

    void decodeAgentTaskableParams() {
    	managedHost = getParamsString(0);
    	if (managedHost == null || managedHost.length() == 0) {
    		throw new RuntimeException("Host name/address is required");
    	}
    }
    


}
