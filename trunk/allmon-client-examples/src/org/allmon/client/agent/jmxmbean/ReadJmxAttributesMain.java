package org.allmon.client.agent.jmxmbean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
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

import sun.tools.jconsole.LocalVirtualMachine;

public class ReadJmxAttributesMain {

	public static void main(String[] args) throws IOException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {

	    LocalVirtualMachine lvm = null;
	    
	    System.out.println("-- get all virtual machines -------------------------");
        Map<Integer, LocalVirtualMachine> map = LocalVirtualMachine.getAllVirtualMachines();
        Iterator<Map.Entry<Integer, LocalVirtualMachine>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, LocalVirtualMachine> pairs = (Map.Entry<Integer, LocalVirtualMachine>)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            //
            lvm = pairs.getValue();
        }
        
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
	            //Descriptor descriptor = mbeanAttributeInfo.getDescriptor();
	            
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
                        
                        for (Object k : compositeType.keySet()) {
                            Object o = compositeDataSupportAttribute.get(k.toString());
                            System.out.println("   > " + mbeanAttributeInfo.getName() + " : " + k.toString() + " : " + o);
                        }
                        
                        //System.out.println("   > " + mbeanAttributeInfo.getName() + " : " + attribute);
                    }
                    
                } catch (Exception e) {
                }
	            	            
            }
	        
        }
	    
	}

}
