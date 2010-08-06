package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.jmx.JmxAttributesReader;
import org.allmon.client.agent.jmx.MBeanAttributeData;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent is responsible for active monitoring of metrics accessible through JMX. 
 * The agent is scanning all accessible virtual machines on the localhost 
 * (for canonical names containing regexp phrase - the first parameter) and retrieves 
 * all numeric values from all MBeans which meets regexp names criteria - the second parameter.
 * 
 * This agent works (compile) with jdk 6 only.
 * 
 */
public class JmxServerAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(JmxServerAgent.class);
    
    private boolean verboseLogging = AllmonCommonConstants.ALLMON_CLIENT_AGENT_JMXSERVERAGENT_VERBOSELOGGING;
    
    // jvm instances parameters
	protected String lvmNamesRegexp = ""; // all local JVMs
    protected String hostName = "";
    protected int port = 0;
    
    // mbeans object/attributes parameters
    protected String mbeansAttributesNamesRegexp = ""; // all attributes
    protected String mbeansObjectName = "";
    protected String mbeansAttributeName = "";
    protected String mbeansAttributeKeyName = "";
    
	public JmxServerAgent(AgentContext agentContext) {
		super(agentContext);
	}
	
	public final MetricMessageWrapper collectMetrics() {
		JmxAttributesReader reader;
		if (!"".equals(mbeansObjectName)) {
			reader = new JmxAttributesReader(mbeansObjectName, mbeansAttributeName, mbeansAttributeKeyName);
		} else if (!"".equals(mbeansAttributesNamesRegexp)) {
			reader = new JmxAttributesReader(mbeansAttributesNamesRegexp);
		} else {
			throw new RuntimeException("Neither MBean specific object name nor regexp search phrase was set");
		}
		
		// collect all mbeans objects attributes data
		List<MBeanAttributeData> attributeDataList;
		if (!"".equals(lvmNamesRegexp)) {
			attributeDataList = reader.getAttributesFromLocal(lvmNamesRegexp);
		} else if (!"".equals(hostName)) {
			attributeDataList = reader.getAttributesFromHost(hostName, port);
		} else {
			throw new RuntimeException("Neither host nor local JVM search phrase was set");
		}
		
		// extract all attributes and create messages
		MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        for (MBeanAttributeData beanAttributeData : attributeDataList) {
            //logger.debug("Creating jmx message: " + beanAttributeData.getJvmId() + ":" + beanAttributeData.getJvmName() + " - " + beanAttributeData.toString());
            MetricMessage metricMessage = MetricMessageFactory.createJmxMessage(
                    beanAttributeData.getJvmId(), beanAttributeData.getJvmName(),
                    beanAttributeData.getMbeanName(), beanAttributeData.getMbeanAttributeName(),
                    beanAttributeData.getValue(), null);
            metricMessageWrapper.add(metricMessage);
            //logger.debug("jmx message created: " + metricMessage.toString());
        }
        return metricMessageWrapper;
	}
		
    public void setLvmNamesRegexp(String lvmNamesRegexp) {
		this.lvmNamesRegexp = lvmNamesRegexp;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public void setMbeansAttributesNamesRegexp(String mbeansAttributesNamesRegexp) {
		this.mbeansAttributesNamesRegexp = mbeansAttributesNamesRegexp;
	}
	public void setMbeansObjectName(String mbeansObjectName) {
		this.mbeansObjectName = mbeansObjectName;
	}
	public void setMbeansAttributeName(String mbeansAttributeName) {
		this.mbeansAttributeName = mbeansAttributeName;
	}
	public void setMbeansAttributeKeyName(String mbeansAttributeKeyName) {
		this.mbeansAttributeKeyName = mbeansAttributeKeyName;
	}

//	public void setHostName(String hostName, int port) {
//		this.hostName = hostName;
//		this.port = port;
//	}
//
//	public void setMbeansObjectName(String mbeansObjectName, String mbeansAttributeName, String mbeansAttributeKeyName) {
//		this.mbeansObjectName = mbeansObjectName;
//		this.mbeansAttributeName = mbeansAttributeName;
//		this.mbeansAttributeKeyName = mbeansAttributeKeyName;
//	}
	
}
