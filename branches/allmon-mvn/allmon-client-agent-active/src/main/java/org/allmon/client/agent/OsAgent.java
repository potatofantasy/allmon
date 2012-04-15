package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
import org.hyperic.sigar.Swap;

public class OsAgent extends ActiveAgent {

	private static final Log logger = LogFactory.getLog(OsAgent.class);
    
    private boolean verboseLogging = AllmonCommonConstants.ALLMON_CLIENT_AGENT_JMXSERVERAGENT_VERBOSELOGGING;

    private String metricType = "ALL";
    
	OsAgent(AgentContext agentContext) {
		super(agentContext);
	}
	
	@Override
	public MetricMessageWrapper collectMetrics() {
		return collectMetrics(metricType);
	}
	
	public static MetricMessageWrapper collectMetrics(String metricType) {
		MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
		final SimpleOSMetricsPackageAgent osAgent = new SimpleOSMetricsPackageAgent(metricMessageWrapper);

		// extract all attributes values and create messages
		try {
			logger.debug("Set metric type is: " + metricType);
			if (metricType.indexOf(OsMetricType.CPU.getTypeName()) >= 0) {
				logger.debug("Collecting OS CPU metrics...");
				osAgent.getCpu();
			} else if (metricType.indexOf(OsMetricType.IO.getTypeName()) >= 0) {
				logger.debug("Collecting OS IO metrics...");
				osAgent.getIo();
			} else if (metricType.indexOf(OsMetricType.PROC.getTypeName()) >= 0) {
				logger.debug("Collecting OS Processes metrics...");
				osAgent.getProc();
			} else if (metricType.indexOf(OsMetricType.MEM.getTypeName()) >= 0) {
				logger.debug("Collecting OS Memory metrics...");
				osAgent.getMem();
			} else if (metricType.indexOf(OsMetricType.SWAP.getTypeName()) >= 0) {
				logger.debug("Collecting OS Swap metrics...");
				osAgent.getSwap();
			} else if (metricType.indexOf("ALL") >= 0) {
				logger.debug("Collecting OS ALL metrics...");
				osAgent.getCpu();
				osAgent.getIo();
				osAgent.getProc();
				osAgent.getMem();
				osAgent.getSwap();
			}
		} catch (SigarException e) {
			logger.debug(e, e);
		}
		
        return metricMessageWrapper;
	}

	public void setMetricType(String metricType) {
		if (metricType != null) {
			this.metricType = metricType.toUpperCase();
		}
	}
    
	public static void main(String[] args) throws SigarException {
		MetricMessageWrapper wrapper = new MetricMessageWrapper();
		SimpleOSMetricsPackageAgent osAgent = new SimpleOSMetricsPackageAgent(wrapper);
		osAgent.getCpu();
		osAgent.getIo();
		osAgent.getProc();
		osAgent.getMem();
		osAgent.getSwap();
		//System.out.println(osAgent.getWrapper().toString());
//		System.out.println(wrapper);
	}
    
}

enum OsMetricType {
	
	//ALL("OSALL"), 
	CPU(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_CPU), 
	IO(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_IO), 
	PROC(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_PROC), 
	MEM(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_MEM), 
	SWAP(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_SWAP), 
	NET(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_NET);
	
	private String typeMnemonic;
	
	private OsMetricType(String typeMnemonic) {
		this.typeMnemonic = typeMnemonic;
	}
	
	public String getTypeName() {
		return typeMnemonic;
	}
	
}

class SimpleOSMetricsPackageAgent {

	private final static SigarProxy sigar = SigarProxyCache.newInstance(new Sigar(), 1000);
	
	private final MetricMessageWrapper wrapper;
	
	SimpleOSMetricsPackageAgent(MetricMessageWrapper wrapper) {
//		System.out.println("OSMetricsPackageAgent initialized----------------------------------------");
		this.wrapper = wrapper;
	}
	
	public void getCpu() throws SigarException {
		String metricTypeName = OsMetricType.CPU.getTypeName();
//		System.out.println("Metric Type: CPU----------------------------------------");
//		System.out.println(sigar.getCpuPerc());
        
		CpuPerc cpu = sigar.getCpuPerc();
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU User Time: ", cpu.getUser(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU Sys Time: ", cpu.getSys(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU Idle Time: ", cpu.getIdle(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU Wait Time: ", cpu.getWait(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU Nice Time: ", cpu.getNice(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU Combined: ", cpu.getCombined(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "CPU Irq Time: ", cpu.getIrq(), null));
	}
	
	public void getIo() throws SigarException {
		String metricTypeName = OsMetricType.IO.getTypeName();
//		System.out.println("Metric Type: IO----------------------------------------");
		
		FileSystem[] fslist = sigar.getFileSystemList();
        for (int i=0; i<fslist.length; i++) {
            if (fslist[i].getType() == FileSystem.TYPE_LOCAL_DISK) {
				FileSystemUsage usage = sigar.getFileSystemUsage(fslist[i].getDirName());
//				System.out.println(usage.toString());
				
				// source
				String devName = fslist[i].getDevName() + "(" + fslist[i].getDirName() + ")";
				
				wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "DiskReads: ", devName, usage.getDiskReads(), null));
				wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "DiskWrites: ", devName, usage.getDiskWrites(), null));
				wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "DiskReadBytes: ", devName, usage.getDiskReadBytes(), null));
				wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "DiskWriteBytes: ", devName, usage.getDiskWriteBytes(), null));
				wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "DiskQueue: ", devName, usage.getDiskQueue(), null));
				wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "DiskServiceTime: ", devName, usage.getDiskServiceTime(), null));
            }
        }
	}
	
	public void getProc() throws SigarException {
		String metricTypeName = OsMetricType.PROC.getTypeName();
//		System.out.println("Metric Type: PROC----------------------------------------");
//		System.out.println(sigar.getProcStat());
		
		ProcStat procStat = sigar.getProcStat();
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Total: ", procStat.getTotal(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Sleeping: ", procStat.getSleeping(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Idle: ", procStat.getIdle(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Running: ", procStat.getRunning(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Zombie: ", procStat.getZombie(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Stopped: ", procStat.getStopped(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Processes Threads: ", procStat.getThreads(), null));
	}
	
	public void getMem() throws SigarException {
		String metricTypeName = OsMetricType.MEM.getTypeName();
//		System.out.println("Metric Type: MEM----------------------------------------");
//		System.out.println(sigar.getMem());
		
		Mem memStat = sigar.getMem();
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem RAM: ", memStat.getRam(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem Total: ", memStat.getTotal(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem Used: ", memStat.getUsed(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem ActualUsed: ", memStat.getActualUsed(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem UsedPercent: ", memStat.getUsedPercent(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem Free: ", memStat.getFree(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem ActualFree: ", memStat.getActualFree(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Mem FreePercent: ", memStat.getFreePercent(), null));
	}
	
	public void getSwap() throws SigarException {
		String metricTypeName = OsMetricType.SWAP.getTypeName();
//		System.out.println("Metric Type: SWAP----------------------------------------");
//		System.out.println(sigar.getSwap());
		
		Swap swapStat = sigar.getSwap();
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Swap Total: ", swapStat.getTotal(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Swap Used: ", swapStat.getUsed(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Swap Free: ", swapStat.getFree(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Swap PageIn: ", swapStat.getPageIn(), null));
		wrapper.add(MetricMessageFactory.createOsMessage(metricTypeName, "Swap PageOut: ", swapStat.getPageOut(), null));
	}
	
	public void getIfconfig() throws SigarException {
//		String metricTypeName = OsMetricType.NET.getTypeName();
//		System.out.println("Metric Type: NET----------------------------------------");
//		// TODO see Ifconfig
//		System.out.println(sigar.getNetInterfaceStat(""));
		
	}
	
	public MetricMessageWrapper getWrapper() {
		return wrapper;
	}
	
}