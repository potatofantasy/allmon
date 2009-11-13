package org.allmon.client.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.tools.jconsole.LocalVirtualMachine;

final class ReadJmxAttributesMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(ReadJmxAttributesMain.class);
    
    List<LocalVirtualMachine> getLocalVirtualMachine(String nameRegexp) {
        logger.debug("-- get virtual machines -------------------------");
        
        Map<Integer, LocalVirtualMachine> map = LocalVirtualMachine.getAllVirtualMachines();
        List<LocalVirtualMachine> lvmList = new ArrayList<LocalVirtualMachine>();
        Iterator<Map.Entry<Integer, LocalVirtualMachine>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, LocalVirtualMachine> pairs = (Map.Entry<Integer, LocalVirtualMachine>)it.next();
            String vmString = 
                "id:" + pairs.getKey() + 
                ", main:" + pairs.getValue() + 
                ", displayName:" + pairs.getValue().displayName() +
                ", connectorAddress:" + pairs.getValue().connectorAddress();
            logger.debug(vmString);
            // check if name matches
            Pattern p = Pattern.compile(".*" + nameRegexp + ".*");
            Matcher m = p.matcher(vmString);
            if (m.find()) {
                //CharSequence cs = m.group();
                lvmList.add(pairs.getValue());
            }
        }
        return lvmList;
    }
    
    List<MBeanAttributeData> getMBeansAttributesData(MBeanServerConnection mbs, String nameRegexp) 
    throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
        logger.debug("- get list of mbeans names - attributes --------------------------");
        
        ArrayList<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
        
        Set<ObjectName> mbeans = mbs.queryNames(null, null);
        for (ObjectName mbean : mbeans) {
            String mbeanDomain = mbean.getDomain();
            logger.debug(mbeanDomain + " : " + mbean + " : " + mbean.getCanonicalKeyPropertyListString());
            
            MBeanInfo mbeanInfo = mbs.getMBeanInfo(mbean);
            MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
            for (int i = 0; i < mbeanAttributeInfos.length; i++) {
                MBeanAttributeInfo mbeanAttributeInfo = mbeanAttributeInfos[i];
                //Descriptor descriptor = mbeanAttributeInfo.getDescriptor();
                logger.debug(" > " + mbeanAttributeInfo.getName() + " : " + mbeanAttributeInfo);
                
                try {
                    Object attribute = mbs.getAttribute(mbean, mbeanAttributeInfo.getName());
                    
                    // sun recommends using this types of complex attributes types
                    // ArrayType, CompositeType, or TabularType
                    // TODO extends types decomposition
                    if (attribute instanceof Number 
                            || attribute instanceof Boolean) {
                        MBeanAttributeData attributeData = new MBeanAttributeData(
                                mbeanDomain, mbeanInfo.getClassName(), mbeanAttributeInfo.getName());
                        attributeData.setNumberValue(attribute);
                        attributeDataList.add(attributeData);
                    } else if (attribute instanceof CompositeDataSupport) {
                        // decompose
                        CompositeDataSupport compositeDataSupportAttribute = (CompositeDataSupport)attribute;
                        CompositeType compositeType = compositeDataSupportAttribute.getCompositeType();
                        
                        //ex: "LastGcInfo" - sun.management.GarbageCollectorImpl / com.sun.management.GarbageCollectorMXBean - GcThreadCount, duration, endTime, id, startTime
                        //ex: "HeapMemoryUsage" - sun.management.MemoryImpl / java.lang.management.MemoryMXBean - {committed, init, max, used}
                        
                        for (String k : compositeType.keySet()) {
                            Object o = compositeDataSupportAttribute.get(k);
                            MBeanAttributeData attributeData = new MBeanAttributeData(
                                    mbeanDomain, mbeanInfo.getClassName(), mbeanAttributeInfo.getName() + ":" + k);
                            attributeData.setNumberValue(o);
                            attributeDataList.add(attributeData);
                        }
                    }
                    
                } catch (Exception e) {
                    //logger.error(e, e);
                }
            }
            
        }
        logger.debug("Found mbeans: " + attributeDataList.size());
        
        return attributeDataList;
    }
    
        
    public class MBeanAttributeData {
        
        private String domainName;
        private String mbeanName;
        private String mbeanAttributeName;
        private double value = 0;
        
        MBeanAttributeData(String domainName, String mbeanName, String mbeanAttributeName) {
            this.domainName = domainName;
            this.mbeanName = mbeanName;
            this.mbeanAttributeName = mbeanAttributeName;
        }
        
        public String toString() {
            return domainName + mbeanName + mbeanAttributeName;
        }
    
        void setNumberValue(Object attribute) {
            if (attribute instanceof Number) {
                setNumberValue((Number)attribute);
            } else if (attribute instanceof Boolean) {
                setNumberValue((Boolean)attribute);
            } else if (attribute instanceof CompositeDataSupport) {
                // composite should be called
            }
        }
        
        void setNumberValue(Number attribute) {
            logger.debug("   > " + mbeanAttributeName + " : " + attribute);
            value = Double.parseDouble(attribute.toString());
        }
        
        void setNumberValue(Boolean attribute) {
            logger.debug("   > " + mbeanAttributeName + " : " + attribute);
            value = "true".equals(attribute.toString())?1:0;
        }
        
    }
    
    
	public static void main(String[] args) throws IOException, NullPointerException, InstanceNotFoundException, ReflectionException, IntrospectionException {

	    ReadJmxAttributesMain jmxReader = new ReadJmxAttributesMain();
	    
        logger.debug("-- get virtual machine -------------------------");
        List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine("AgentAggregatorMain");
	    LocalVirtualMachine lvm = lvmList.get(0);
	    
        logger.debug("-- connect to the last vm on the list -------------------------");
        JMXServiceURL jmxUrl = null;
	    if (lvm != null) {
            if (!lvm.isManageable()) {
                lvm.startManagementAgent();
                if (!lvm.isManageable()) {
                    throw new IOException(lvm + "not manageable");
                }
            }
            jmxUrl = new JMXServiceURL(lvm.connectorAddress());
        }
	    
	    // get server
	    JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
	    MBeanServerConnection mbs = jmxc.getMBeanServerConnection();
	    
	    // get local server
	    //MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	    logger.debug("- get list of mbeans (classes) instances --------------------------");
        Set<ObjectInstance> mbeanInstances = mbs.queryMBeans(null, null);
        for (ObjectInstance mbeanInstance : mbeanInstances) {
            logger.debug(mbeanInstance);
        }

	    logger.debug("- get list of mbeans names - attributes --------------------------");
	    List<MBeanAttributeData> attributeDataList = jmxReader.getMBeansAttributesData(mbs, ".*");

	    
	}

}
