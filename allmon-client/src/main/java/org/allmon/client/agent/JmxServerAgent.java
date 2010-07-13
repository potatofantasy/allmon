package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.jmx.LocalVirtualMachineDescriptor;
import org.allmon.client.agent.jmx.JmxAttributesReader;
import org.allmon.client.agent.jmx.MBeanAttributeData;
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
    
    private JmxAttributesReader jmxReader = new JmxAttributesReader();
    
	protected String lvmNamesRegexp = ""; // all local JVMs
    protected String mbeansAttributesNamesRegexp = ""; // all attributes
    
	public JmxServerAgent(AgentContext agentContext) {
		super(agentContext);
	}

	public final MetricMessageWrapper collectMetrics() {
	    MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
	    List<LocalVirtualMachineDescriptor> lvmList = jmxReader.getLocalVirtualMachine(lvmNamesRegexp, true);
        for (LocalVirtualMachineDescriptor localVirtualMachine : lvmList) {
            List<MBeanAttributeData> attributeDataList;
            try {
                attributeDataList = jmxReader.getMBeansAttributesData(localVirtualMachine, mbeansAttributesNamesRegexp, true);
                // extract all attributes and create messages
                for (MBeanAttributeData beanAttributeData : attributeDataList) {
                    //logger.debug("Creating jmx message: " + beanAttributeData.getJvmId() + ":" + beanAttributeData.getJvmName() + " - " + beanAttributeData.toString());
                    MetricMessage metricMessage = MetricMessageFactory.createJmxMessage(
                            beanAttributeData.getJvmId(), beanAttributeData.getJvmName(),
                            beanAttributeData.getMbeanName(), beanAttributeData.getMbeanAttributeName(),
                            beanAttributeData.getValue(), null);
                    metricMessageWrapper.add(metricMessage);
                    //logger.debug("jmx message created: " + metricMessage.toString());
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
	    return metricMessageWrapper;
	}

//    void decodeAgentTaskableParams() {
//        lvmNamesRegexp = getParamsString(0);
//        mbeansAttributesNamesRegexp = getParamsString(1);
//    }

    public void setLvmNamesRegexp(String lvmNamesRegexp) {
		this.lvmNamesRegexp = lvmNamesRegexp;
	}

	public void setMbeansAttributesNamesRegexp(String mbeansAttributesNamesRegexp) {
		this.mbeansAttributesNamesRegexp = mbeansAttributesNamesRegexp;
	}
	
}
