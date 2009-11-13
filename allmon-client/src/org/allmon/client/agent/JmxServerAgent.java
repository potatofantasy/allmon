package org.allmon.client.agent;

import java.util.List;

import org.allmon.client.agent.JmxAttributesReader.MBeanAttributeData;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.tools.jconsole.LocalVirtualMachine;

public class JmxServerAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(JmxServerAgent.class);
    
    private JmxAttributesReader jmxReader = new JmxAttributesReader();
    
    protected String lvmNamesRegexp = ""; // all local JVMs
    protected String mbeansAttributesNamesRegexp = ""; // all attributes
    
	public JmxServerAgent(AgentContext agentContext) {
		super(agentContext);
	}

	public MetricMessageWrapper collectMetrics() {
	    MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
	    List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(lvmNamesRegexp);
        for (LocalVirtualMachine localVirtualMachine : lvmList) {
            List<MBeanAttributeData> attributeDataList;
            try {
                attributeDataList = jmxReader.getMBeansAttributesData(localVirtualMachine, mbeansAttributesNamesRegexp);
                // extract all attributes and create messages
                for (MBeanAttributeData beanAttributeData : attributeDataList) {
                    MetricMessage metricMessage = MetricMessageFactory.createJMXMessage(
                            beanAttributeData.getJvmId(),
                            beanAttributeData.getJvmName(),
                            beanAttributeData.getDomainName(),
                            beanAttributeData.getMbeanName(),
                            beanAttributeData.getMbeanAttributeName(),
                            beanAttributeData.getValue(),
                            null);
                    metricMessageWrapper.add(metricMessage);
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
	    return metricMessageWrapper;
	}

    void decodeAgentTaskableParams() {
    }

}
