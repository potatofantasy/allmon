package org.allmon.client.agent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.allmon.client.agent.snmp.ProcessPerfData;
import org.allmon.client.agent.snmp.SnmpException;
import org.allmon.client.agent.snmp.SnmpHostApi;
import org.allmon.client.agent.snmp.SnmpSettings;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SnmpHostAgent extends ActiveAgent {

	private static final Log logger = LogFactory.getLog(SnmpHostAgent.class);

	private String managedHost;
	private boolean isCollectCpuLoad = false;
	private Set<String> processSet = new HashSet<String>();

	public SnmpHostAgent(AgentContext agentContext) {
		super(agentContext);
	}

	/**
	 * Collects CPU load or/and processes metrics.
	 */
	MetricMessageWrapper collectMetrics() {
		logger.info("Collecting metrics for host: " + managedHost);
		MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();

		SnmpSettings settings = new SnmpSettings();
		settings.setIPAddress(managedHost);
		SnmpHostApi snmpHostAPI = new SnmpHostApi(settings);

		if (isCollectCpuLoad) {
			List<String> cpuLoadList = snmpHostAPI.getCpuLoad();
			collectCpuLoad(metricMessageWrapper, cpuLoadList);
		}

		if (processSet.size() > 0) {
			List<ProcessPerfData> procTable = snmpHostAPI.getProcessList();
			collectProcessPerfData(metricMessageWrapper, procTable);
		}

		return metricMessageWrapper;
	}

	private void collectCpuLoad(MetricMessageWrapper metricMessageWrapper,
			List<String> cpuLoadList) {
		int cpuNum = 1;
		for (String cpuLoad : cpuLoadList) {
			Exception snmpException = null;
			double cpuLoadValue = 0;
			if (cpuLoad.indexOf(SnmpHostApi.ERROR_STR) == -1) {
				// no error string
				cpuLoadValue = Double.valueOf(cpuLoad);
			} else {
				// snmpException = new
				// SnmpException(cpuLoad.substring(SnmpHostApi.ERROR_STR.length()));
				snmpException = new SnmpException(cpuLoad
						.substring(SnmpHostApi.ERROR_STR.length()));
			}
			MetricMessage metricMessage = MetricMessageFactory
					.createSnmpOsMessage(
							AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_CPULOAD,
							"CPU" + cpuNum++, cpuLoadValue, snmpException);
			metricMessageWrapper.add(metricMessage);
		}
	}

	private void collectProcessPerfData(
			MetricMessageWrapper metricMessageWrapper,
			List<ProcessPerfData> procTable) {
		for (ProcessPerfData p : procTable) {
			if (processSet.contains(p.getName())) {
				MetricMessage metricMessage = MetricMessageFactory
						.createSnmpOsMessage(
								AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_PROCESS_CPU_TIME,
								p.getName() + ":" + p.getId(), p.getCpuTime(),
								null);
				metricMessageWrapper.add(metricMessage);
				metricMessage = MetricMessageFactory
						.createSnmpOsMessage(
								AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_PROCESS_MEMORY,
								p.getName() + ":" + p.getId(), p.getMemory(),
								null);
				metricMessageWrapper.add(metricMessage);

			}

		}
	}

//	/**
//	 * Decodes input parameters for the Agent.
//	 * 
//	 * At least 2 params are required. 1st is always Host name/address 2nd can
//	 * be -cpu or the process name. The rest of the params are the names of
//	 * processes
//	 */
//	void decodeAgentTaskableParams() {
//		// 1st param
//		managedHost = getParamsString(0);
//		if (managedHost == null || managedHost.length() == 0) {
//			throw new RuntimeException("Host name/address is required");
//		}
//
//		// 2nd param
//		String param2 = getParamsString(1);
//		if (param2.equalsIgnoreCase("-cpu")) {
//			isCollectCpuLoad = true;
//		} else {
//			processSet.add(param2);
//		}
//
//		// rest of the params
//		int i = 2;
//		String p = getParamsString(i++);
//		while (p != null) {
//			processSet.add(p);
//			p = getParamsString(i++);
//		}
//	}

}
