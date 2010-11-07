package org.allmon.client.agent.jmx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JmxAttributesReader {

    private static final Log logger = LogFactory.getLog(JmxAttributesReader.class);
    
    private boolean verboseLogging = AllmonCommonConstants.ALLMON_CLIENT_AGENT_JMXSERVERAGENT_VERBOSELOGGING;
    
    // mbeans object/attributes parameters
    private String mbeansAttributesNamesRegexp = ""; // objects/attributes search phrase
    private String mbeansObjectName = "";
    private String mbeansAttributeName = "";
    private String mbeansAttributeKeyName = ""; // by default all attributes keys will be takes

    public JmxAttributesReader(String mbeansAttributesNamesRegexp) {
		this.mbeansAttributesNamesRegexp = mbeansAttributesNamesRegexp;
	}
    
    public JmxAttributesReader(String mbeansObjectName, 
    		String mbeansAttributeName) {
		this.mbeansObjectName = mbeansObjectName;
		this.mbeansAttributeName = mbeansAttributeName;
	}
    
    public JmxAttributesReader(String mbeansObjectName, 
    		String mbeansAttributeName, String mbeansAttributeKeyName) {
		this.mbeansObjectName = mbeansObjectName;
		this.mbeansAttributeName = mbeansAttributeName;
		this.mbeansAttributeKeyName = mbeansAttributeKeyName;
	}
    
	/**
     * Search in all local jvm instances processes meeting name expression
     * 
     * @param nameRegexp
     * @param restrictive
     * @return
     */
	public List<MBeanAttributeData> getAttributesFromLocal(String lvmNamesRegexp) {
		if (verboseLogging) {
			logger.debug("Reading JMX data from local instance");
		}
		
		List<MBeanAttributeData> attributeDataListAll = new ArrayList<MBeanAttributeData>();
        
	    List<LocalVirtualMachineDescriptor> lvmList = getLocalVirtualMachine(lvmNamesRegexp, false);
	    for (LocalVirtualMachineDescriptor localVirtualMachine : lvmList) {
            List<MBeanAttributeData> attributeDataList;
            try {
                MBeanAttributeExtractor extractor = null;
                try {
        	        extractor = new MBeanAttributeExtractor(localVirtualMachine);
                	if (!"".equals(mbeansAttributesNamesRegexp)) {
                		attributeDataList = extractor.getMBeansAttributesData(
                				mbeansAttributesNamesRegexp, true);
                	} else {
                		attributeDataList = extractor.getMBeansAttributesData(
                				mbeansObjectName, mbeansAttributeName, mbeansAttributeKeyName);
                	}
                } finally {
                    if (extractor != null) {
                    	extractor.disconnect();
                    }
                }
                attributeDataListAll.addAll(attributeDataList);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
	    return attributeDataListAll;
	}
	
	/**
	 * Search in a jvm instances specified by host and port 
	 * 
	 * @param hostName
	 * @param port
	 * @return
	 */
	public List<MBeanAttributeData> getAttributesFromHost(String hostName, int port) {
		if (verboseLogging) {
			logger.debug("Reading JMX data from instance pointed by host:port");
		}
		
		List<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
        try {
        	MBeanAttributeExtractor extractor = null;
            try {
    	        extractor = new MBeanAttributeExtractor(hostName, port);
    	        if (!"".equals(mbeansAttributesNamesRegexp)) {
            		attributeDataList = extractor.getMBeansAttributesData(
            				mbeansAttributesNamesRegexp, true);
            	} else {
            		attributeDataList = extractor.getMBeansAttributesData(
            				mbeansObjectName, mbeansAttributeName, mbeansAttributeKeyName);
            	}
            } finally {
                if (extractor != null) {
                	extractor.disconnect();
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return attributeDataList;
	}
	
	List<LocalVirtualMachineDescriptor> getLocalVirtualMachine(String nameRegexp, boolean restrictive) {
		if (verboseLogging) {
			logger.debug("Get list of all local virtual machines");
		}
        
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
            if (verboseLogging) {
            	logger.debug("Found active virtual machine instance: " + vmString);
            }
            // check if name matches search phrase
            if (vmString.matches(nameRegexp)) {
                lvmList.add(pairs.getValue());
            }
        }
        return lvmList;
    }
    
}