package org.allmon.client.agent.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.allmon.common.AllmonCommonConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MBeanAttributeExtractor {
	
	private static final Log logger = LogFactory.getLog(MBeanAttributeExtractor.class);
    
	private boolean verboseLogging = AllmonCommonConstants.ALLMON_CLIENT_AGENT_JMXSERVERAGENT_VERBOSELOGGING;
    	
	private long jvmId;
	private String jvmName;
	    
	private JMXConnector jmxConnector;
    
	MBeanAttributeExtractor(LocalVirtualMachineDescriptor lvm) throws IOException {
    	jmxConnector = connect(lvm);
    }
    
	MBeanAttributeExtractor(String hostname, int port) throws IOException {
    	jmxConnector = connect(hostname, port);
    }
	
    public void disconnect() throws IOException {
    	if (jmxConnector != null) {
    		jmxConnector.close();
        }
    }
    
    private JMXConnector connect(LocalVirtualMachineDescriptor lvm) throws IOException {
        String lvmString = lvm.getCannonicalName();
        
        if (verboseLogging) {
        	logger.debug("Connecting to local jvm: " + lvmString);
        }
        
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
        String connectorAddress = lvm.connectorAddress();
        JMXServiceURL jmxUrl = new JMXServiceURL(connectorAddress);
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
        
        jvmId = lvm.getVMid();
        jvmName = "host=" + jmxUrl.getHost() + ":" + jmxUrl.getPort() + "//process:" + lvmString;
        
        if (verboseLogging) {
        	logger.debug("Connected to local jvm: " + lvmString + ", connector addres: " + connectorAddress);
        }
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
     * @throws IOException 
     */
    private JMXConnector connect(String hostname, int port) throws IOException {
        String hostPort = hostname + ":" + port;
        
        if (verboseLogging) {
        	logger.debug("Connecting to jvm: " + hostPort);
        }
        
        // Create an RMI connector client and connect it to the RMI connector server
        String urlPath = "/jndi/rmi://" + hostPort + "/jmxrmi";
        JMXConnector jmxc = null;
        try {
            JMXServiceURL jmxUrl = new JMXServiceURL("rmi", "", 0, urlPath);
            jmxc = JMXConnectorFactory.connect(jmxUrl);
            
            jvmId = 0;
            jvmName = "host=" + hostPort + "//process:remote";
            
            if (verboseLogging) {
            	logger.debug("Connected to jvm: " + hostPort + ", via url: " + urlPath);
            }
            
            return jmxc;
        } catch (MalformedURLException e) {
        	throw new IOException("Wrong url: " + e.getMessage()); 
        } catch (IOException e) {
        	throw new IOException("Communication error: " + e.getMessage());
        }
    }
    
    /**
     * objectName;attributeName/keyName
     * java.lang:type=Memory;HeapMemoryUsage/used
     * 
     * @param objectName
     * @param attributeName
     * @param keyName
     * @return
     * @throws Exception
     */
	public List<MBeanAttributeData> getMBeansAttributesData(String objectNameString, String attributeName, String attributeKeyName) throws Exception {
		List<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
		MBeanServerConnection mbsConnection = jmxConnector.getMBeanServerConnection();
		ObjectName objectName = new ObjectName(objectNameString);
		Object attribute = mbsConnection.getAttribute(objectName, attributeName);
		addAttributeDataToList(attributeDataList, 
    			objectName, "", 
    			attribute, attributeName, attributeKeyName, 
    			"");
		return attributeDataList;
	}
    	
    public List<MBeanAttributeData> getMBeansAttributesData(String nameRegexp, boolean restrictive) throws IOException {
        if (!restrictive) {
            nameRegexp = ".*" + nameRegexp + ".*";
        }
        
    	// result collection
        List<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
        
        // connect
        MBeanServerConnection mbsConnection = jmxConnector.getMBeanServerConnection();
        
        //QueryExp queryExp = Query.eq(Query.attr(queryAttrStr), Query.value(integer)); // TODO finish pre-filtering
        //Set<ObjectInstance> objectInstances = mbsConnection.queryMBeans(null, queryExp);
        Set<ObjectInstance> objectInstances = mbsConnection.queryMBeans(null, null); // get all MBeans
        for (ObjectInstance objectInstance : objectInstances) {
            String objectClassName = objectInstance.getClassName();
            ObjectName objectName = objectInstance.getObjectName();
            
            try {
                
                MBeanInfo mbeanInfo = mbsConnection.getMBeanInfo(objectName);
                MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
                for (MBeanAttributeInfo mbeanAttributeInfo : mbeanAttributeInfos) {
                	String attributeName = mbeanAttributeInfo.getName();
                    //Descriptor descriptor = mbeanAttributeInfo.getDescriptor();
                    //logger.debug(" > " + attributeName + " : " + mbeanAttributeInfo);
                    
                    try {
                    	Object attribute = mbsConnection.getAttribute(objectName, attributeName);
                        
                        addAttributeDataToList(attributeDataList, 
                    			objectName, objectClassName, 
                    			attribute, attributeName, "",
                    			nameRegexp);
                        
                    } catch (Exception e) {
                        //logger.error(e, e);
                    }
                }
            
            } catch (Exception e) {
                //logger.error(e, e);
            }
        }

        if (verboseLogging) {
        	logger.debug("Found MBeans: " + objectInstances.size() + " and MBean attributes: " + attributeDataList.size());
        }
        
        return attributeDataList;
    }

    private void addAttributeDataToList(List<MBeanAttributeData> attributeDataList, 
    		ObjectName objectName, String objectClassName,
    		Object attribute, String attributeName, String attributeKeyName, 
    		String nameRegexp) {
    	
    	String objectNameString = objectName.toString(); // String objectCanonicalName = objectName.getCanonicalName();
        String objectDomain = objectName.getDomain();
        
    	// Sun recommends using this types of complex attributes types
        // ArrayType, CompositeType, or TabularType
        // TODO extends types decomposition
        if (attribute instanceof Number || attribute instanceof Boolean) {
            MBeanAttributeData attributeData = new MBeanAttributeData(
                    jvmId, jvmName, 
                    objectDomain, objectClassName, objectNameString, attributeName);
            attributeData.setNumberValue(attribute);
            if ("".equals(nameRegexp) || attributeData.toString().matches(nameRegexp)) {
            	if (verboseLogging) {
            		logger.debug("Taking attribute data: " + attributeData);
            	}
            	attributeDataList.add(attributeData);
            }
        } else if (attribute instanceof CompositeDataSupport) {
            // decompose
            CompositeDataSupport cdsAttribute = (CompositeDataSupport)attribute;
            CompositeType compositeType = cdsAttribute.getCompositeType();
            
            //ex: "LastGcInfo" - sun.management.GarbageCollectorImpl / com.sun.management.GarbageCollectorMXBean - GcThreadCount, duration, endTime, id, startTime
            //ex: "HeapMemoryUsage" - sun.management.MemoryImpl / java.lang.management.MemoryMXBean - {committed, init, max, used}
            
            for (Object key : compositeType.keySet()) {
            	String keyName = key.toString();
                if ("".equals(attributeKeyName) || keyName.equals(attributeKeyName)) {
            		Object o = cdsAttribute.get(keyName);
	                MBeanAttributeData attributeData = new MBeanAttributeData(
	                        jvmId, jvmName, 
	                        objectDomain, objectClassName, objectNameString, attributeName, keyName);
	                attributeData.setNumberValue(o);
	                if ("".equals(nameRegexp) || attributeData.toString().matches(nameRegexp)) {
	                	if (verboseLogging) {
	                		logger.debug("Taking attribute data: " + attributeData);
	                	}
	                	attributeDataList.add(attributeData);
	                }
            	}
            }
        }
    }
    
}
