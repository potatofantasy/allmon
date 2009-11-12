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

class ReadJmxAttributesMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(ReadJmxAttributesMain.class);
    
    static List<LocalVirtualMachine> getLocalVirtualMachine(String nameRegexp) {
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
            //
            Pattern p = Pattern.compile(".*" + nameRegexp + ".*");
            Matcher m = p.matcher(vmString);
            if (m.find()) {
                //CharSequence cs = m.group();
                lvmList.add(pairs.getValue());
            }
        }
        return lvmList;
    }
    
    
	public static void main(String[] args) throws IOException, NullPointerException, InstanceNotFoundException, ReflectionException, IntrospectionException {

        System.out.println("-- get virtual machine -------------------------");
        List<LocalVirtualMachine> lvmList = getLocalVirtualMachine("AgentAggregatorMain");
	    LocalVirtualMachine lvm = lvmList.get(0);
	    
        System.out.println("-- connect to the last vm on the list -------------------------");
        JMXServiceURL jmxUrl = null;
	    if (lvm != null) {
            if (!lvm.isManageable()) {
                lvm.startManagementAgent();
                if (!lvm.isManageable()) {
                    // FIXME: what to throw
                    throw new IOException(lvm + "not manageable");
                }
            }
            if (jmxUrl == null) {
                jmxUrl = new JMXServiceURL(lvm.connectorAddress());
            }
        }
	    
	    // get server
	    JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
	    MBeanServerConnection mbs = jmxc.getMBeanServerConnection();
	    
	    // get local server
	    //MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	    
        System.out.println("- get list of domains --------------------------");
	    String[] domains = mbs.getDomains();
	    System.out.println(Arrays.toString(domains));
	    for (String domain : domains) {
	        System.out.println(domain);
        }
	    
	    System.out.println("- get list of mbeans names --------------------------");
	    Set<ObjectName> mbeans = mbs.queryNames(null, null);
	    for (ObjectName mbean : mbeans) {
	    	System.out.println(mbean + 
	    			" - " + mbean.getCanonicalKeyPropertyListString());
        }
	    
	    System.out.println("- get list of mbeans (classes) instances --------------------------");
        Set<ObjectInstance> mbeanInstances = mbs.queryMBeans(null, null);
        for (ObjectInstance mbeanInstance : mbeanInstances) {
            System.out.println(mbeanInstance);
        }

	    System.out.println("- get list of mbeans names - attributes --------------------------");
	    mbeans = mbs.queryNames(null, null);
	    for (ObjectName mbean : mbeans) {
	        System.out.println(mbean + " : " + mbean.getCanonicalKeyPropertyListString());
	        
	        MBeanInfo mbeanInfo = mbs.getMBeanInfo(mbean);
	        MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
	        for (int i = 0; i < mbeanAttributeInfos.length; i++) {
	            MBeanAttributeInfo mbeanAttributeInfo = mbeanAttributeInfos[i];
	            Descriptor descriptor = mbeanAttributeInfo.getDescriptor();
	            
	            System.out.println(" > " + mbeanAttributeInfo.getName() + " : " + mbeanAttributeInfo);
                
	            try {
                    Object attribute = mbs.getAttribute(mbean, mbeanAttributeInfo.getName());
                    
                    if (attribute instanceof Number) {
                        System.out.println("   > " + mbeanAttributeInfo.getName() + " : " + attribute);
                    } else if (attribute instanceof Boolean) {
                        System.out.println("   > " + mbeanAttributeInfo.getName() + " : " + attribute);
                    } else if (attribute instanceof CompositeDataSupport) {
                        // decompose
                        CompositeDataSupport compositeDataSupportAttribute = (CompositeDataSupport)attribute;
                        CompositeType compositeType = compositeDataSupportAttribute.getCompositeType();
                        
                        //ex: "LastGcInfo" - sun.management.GarbageCollectorImpl / com.sun.management.GarbageCollectorMXBean - GcThreadCount, duration, endTime, id, startTime
                        //ex: "HeapMemoryUsage" - sun.management.MemoryImpl / java.lang.management.MemoryMXBean - {committed, init, max, used}
                        
                        for (String k : compositeType.keySet()) {
                            Object o = compositeDataSupportAttribute.get(k);
                            System.out.println("   > " + mbeanAttributeInfo.getName() + " : " + k + " : " + o);
                        }
                        
                        //System.out.println("   > " + mbeanAttributeInfo.getName() + " : " + attribute);
                    }
                    
                } catch (Exception e) {
                }
	            	            
            }
	        
        }
	    
	}

}
