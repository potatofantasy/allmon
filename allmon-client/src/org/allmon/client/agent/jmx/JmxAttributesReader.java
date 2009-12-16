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
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JmxAttributesReader {

    private static final Log logger = LogFactory.getLog(JmxAttributesReader.class);
    
    // TODO replace LocalVirtualMachine and add consistent toString implementation 
    public List<LocalVirtualMachineDescriptor> getLocalVirtualMachine(String nameRegexp, boolean restrictive) {
        logger.debug("-- get virtual machines -------------------------");
        
        if (!restrictive) {
            nameRegexp = ".*" + nameRegexp + ".*";
        }
        
        Map<Integer, LocalVirtualMachineDescriptor> map = new LocalVirtualMachineManager().getVirtualMachines();
        List<LocalVirtualMachineDescriptor> lvmList = new ArrayList<LocalVirtualMachineDescriptor>();
        Iterator<Map.Entry<Integer, LocalVirtualMachineDescriptor>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, LocalVirtualMachineDescriptor> pairs = (Map.Entry<Integer, LocalVirtualMachineDescriptor>)it.next();
            String vmString = 
                "id:" + pairs.getKey() + 
                ", name:" + pairs.getValue() +
                ", connectorAddress:" + pairs.getValue().connectorAddress();
            logger.debug(vmString);
            // check if name matches
            if (vmString.matches(nameRegexp)) {
                lvmList.add(pairs.getValue());
            }
        }
        return lvmList;
    }
    
    //private MBeanServerConnection connect(LocalVirtualMachine lvm) throws IOException {
    private JMXConnector connect(LocalVirtualMachineDescriptor lvm) throws IOException {
        String lvmString = lvm.getCannonicalName();
        
        logger.debug("connecting to local jvm: " + lvmString);
        
        JMXServiceURL jmxUrl = null;
        if (!lvm.isManageable()) {
            if (lvm.isAttachable()) {
                lvm.startManagementAgent(); // 
                if (!lvm.isManageable()) {
                    throw new IOException(lvmString + " is not manageable");
                }
            } else {
                throw new IOException(lvmString + " is not attachable");
            }
        }
        jmxUrl = new JMXServiceURL(lvm.connectorAddress());
        // get server
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null); // XXX 
        return jmxc;
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
     * TODO change return type to JMXConnector
     * 
     * @param hostname
     * @param port
     * @return
     */
    //private MBeanServerConnection connect(String hostname, int port) {
    private JMXConnector connect(String hostname, int port) {
        String hostPort = hostname + ":" + port;
        
        logger.debug("connecting to remote jvm: " + hostPort);

        // Create an RMI connector client and connect it to the RMI connector server
        String urlPath = "/jndi/rmi://" + hostPort + "/jmxrmi";
        //MBeanServerConnection server = null;
        JMXConnector jmxc = null;
        try {
            JMXServiceURL url = new JMXServiceURL("rmi", "", 0, urlPath);
            jmxc = JMXConnectorFactory.connect(url);
            //server = jmxc.getMBeanServerConnection();
        } catch (MalformedURLException e) {
            logger.error("Wrong url: " + e.getMessage()); 
        } catch (IOException e) {
            logger.error("Communication error: " + e.getMessage());
        }
        return jmxc;
    }
    
    public List<MBeanAttributeData> getMBeansAttributesData(LocalVirtualMachineDescriptor lvm, String nameRegexp, boolean restrictive) 
    throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
        logger.debug("-- get list of mbeans names - attributes --------------------------");

        long jvmId = lvm.getVMid();
        String jvmName = lvm.toString();
//        logger.debug("connecting to local jvm: " + jvmId + ":" + jvmName);
        
        if (!restrictive) {
            nameRegexp = ".*" + nameRegexp + ".*";
        }
        
        // result collection
        ArrayList<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
        
        //MBeanServerConnection mbs = connect(lvm); // creating a connection thread, which has to be closed
        JMXConnector jmxc = connect(lvm);
        
        try {
            // connect
            MBeanServerConnection mbs = jmxc.getMBeanServerConnection();
            
            Set<ObjectInstance> objectInstances = mbs.queryMBeans(null, null);
    //        Set<ObjectInstance> objectInstances = mbs.queryMBeans(null, objectNameSearch);
            for (ObjectInstance objectInstance : objectInstances) {
                String objectClassName = objectInstance.getClassName();
                ObjectName objectName = objectInstance.getObjectName();
                String objectNameString = objectName.toString(); // String objectCanonicalName = objectName.getCanonicalName();
                String objectDomain = objectName.getDomain();
                
                MBeanInfo mbeanInfo = mbs.getMBeanInfo(objectName);
                MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
                for (MBeanAttributeInfo mbeanAttributeInfo : mbeanAttributeInfos) {
    			    //Descriptor descriptor = mbeanAttributeInfo.getDescriptor();
    //                logger.debug(" > " + mbeanAttributeInfo.getName() + " : " + mbeanAttributeInfo);
                    
                    try {
                        Object attribute = mbs.getAttribute(objectName, mbeanAttributeInfo.getName());
                        
                        // sun recommends using this types of complex attributes types
                        // ArrayType, CompositeType, or TabularType
                        // TODO extends types decomposition
                        if (attribute instanceof Number 
                                || attribute instanceof Boolean) {
                            MBeanAttributeData attributeData = new MBeanAttributeData(
                                    jvmId, jvmName, 
                                    objectDomain, objectClassName, objectNameString, mbeanAttributeInfo.getName());
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
                                MBeanAttributeData attributeData = new MBeanAttributeData(
                                        jvmId, jvmName, 
                                        objectDomain, objectClassName, objectNameString, mbeanAttributeInfo.getName() + "/" + k.toString());
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
            //logger.debug("Found MBeans: " + objectNames.size() + " and MBean attributes: " + attributeDataList.size());
            
        } finally {
            // disconnect
            if (jmxc != null) {
                jmxc.close();
            }
        }
        
        return attributeDataList;
    }
    
}
