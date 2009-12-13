package org.allmon.client.agent.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.tools.jconsole.LocalVirtualMachine;

public final class JmxAttributesReader {

    private static final Log logger = LogFactory.getLog(JmxAttributesReader.class);
    
    public List<LocalVirtualMachine> getLocalVirtualMachine(String nameRegexp) {
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
            if (vmString.matches(".*" + nameRegexp + ".*")) {
                lvmList.add(pairs.getValue());
            }
        }
        return lvmList;
    }
    
    private MBeanServerConnection connect(LocalVirtualMachine lvm) throws IOException {
        logger.debug("connecting to local jvm: id:" + lvm.vmid());
        
        JMXServiceURL jmxUrl = null;
        //if (lvm != null) {
            if (!lvm.isManageable()) {
                lvm.startManagementAgent();
                if (!lvm.isManageable()) {
                    throw new IOException(lvm + " not manageable");
                }
            }
            jmxUrl = new JMXServiceURL(lvm.connectorAddress());
        //}
        // get server
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
        // connect
        MBeanServerConnection mbs = jmxc.getMBeanServerConnection();
        return mbs;
    }
    
    /**
     * Establish a connection with the remote application
     * 
     * You can modify the urlPath to the address of the JMX agent
     * of your application if it has a different URL.
     * 
     * You can also modify the following code to take 
     * username and password for client authentication.
     * 
     * @param hostname
     * @param port
     * @return
     */
    private MBeanServerConnection connect(String hostname, int port) {
        String hostPort = hostname + ":" + port;
        
        logger.debug("connecting to remote jvm: " + hostPort);

        // Create an RMI connector client and connect it to the RMI connector server
        String urlPath = "/jndi/rmi://" + hostPort + "/jmxrmi";
        MBeanServerConnection server = null;        
        try {
            JMXServiceURL url = new JMXServiceURL("rmi", "", 0, urlPath);
            JMXConnector jmxc = JMXConnectorFactory.connect(url);
            server = jmxc.getMBeanServerConnection();
        } catch (MalformedURLException e) {
            logger.error("Wrong url: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Communication error: " + e.getMessage());
        }
        return server;
    }
    
    public List<MBeanAttributeData> getMBeansAttributesData(LocalVirtualMachine lvm, String nameRegexp) 
    throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
        logger.debug("-- get list of mbeans names - attributes --------------------------");

        long jvmId = lvm.vmid();
        String jvmName = lvm.displayName();
//        logger.debug("connecting to local jvm: " + jvmId + ":" + jvmName);
        
        nameRegexp = ".*" + nameRegexp + ".*";

        // result collection
        ArrayList<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
        
        MBeanServerConnection mbs = connect(lvm);
        
        // get local server // MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> mbeans = mbs.queryNames(null, null);
        for (ObjectName mbean : mbeans) {
            String mbeanDomain = mbean.getDomain();
//            logger.debug(mbeanDomain + " : " + mbean + " : " + mbean.getCanonicalKeyPropertyListString());
            
            MBeanInfo mbeanInfo = mbs.getMBeanInfo(mbean);
            MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
            for (MBeanAttributeInfo mbeanAttributeInfo : mbeanAttributeInfos) {
			    //Descriptor descriptor = mbeanAttributeInfo.getDescriptor();
//                logger.debug(" > " + mbeanAttributeInfo.getName() + " : " + mbeanAttributeInfo);
                
                try {
                    Object attribute = mbs.getAttribute(mbean, mbeanAttributeInfo.getName());
                    
                    // sun recommends using this types of complex attributes types
                    // ArrayType, CompositeType, or TabularType
                    // TODO extends types decomposition
                    if (attribute instanceof Number 
                            || attribute instanceof Boolean) {
                        MBeanAttributeData attributeData = new MBeanAttributeData(jvmId, jvmName, mbeanDomain, 
                                mbeanInfo.getClassName(), mbeanAttributeInfo.getName());
                        attributeData.setNumberValue(attribute);
                        if (attributeData.toString().matches(nameRegexp)) {
                            attributeDataList.add(attributeData);
                        }
                    } else if (attribute instanceof CompositeDataSupport) {
                        // decompose
                        CompositeDataSupport compositeDataSupportAttribute = (CompositeDataSupport)attribute;
                        CompositeType compositeType = compositeDataSupportAttribute.getCompositeType();
                        
                        //ex: "LastGcInfo" - sun.management.GarbageCollectorImpl / com.sun.management.GarbageCollectorMXBean - GcThreadCount, duration, endTime, id, startTime
                        //ex: "HeapMemoryUsage" - sun.management.MemoryImpl / java.lang.management.MemoryMXBean - {committed, init, max, used}
                        
                        for (Object k : compositeType.keySet()) {
                            Object o = compositeDataSupportAttribute.get(k.toString());
                            MBeanAttributeData attributeData = new MBeanAttributeData(jvmId, jvmName, mbeanDomain, 
                                    mbeanInfo.getClassName(), mbeanAttributeInfo.getName() + ":" + k.toString());
                            attributeData.setNumberValue(o);
                            if (attributeData.toString().matches(nameRegexp)) {
                                attributeDataList.add(attributeData);
                            }
                        }
                    }
                    
                } catch (Exception e) {
                    //logger.error(e, e);
                }
            }
        }
        logger.debug("Found MBeans: " + mbeans.size() + " and MBean attributes: " + attributeDataList.size());
        
        return attributeDataList;
    }
    
}