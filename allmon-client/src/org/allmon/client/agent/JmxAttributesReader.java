package org.allmon.client.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.management.ConnectorAddressLink;
import sun.tools.jconsole.LocalVirtualMachine;

final class JmxAttributesReader {

    private static final Log logger = LogFactory.getLog(JmxAttributesReader.class);
    
    JmxAttributesReader() {
    }
    
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
            if (vmString.matches(".*" + nameRegexp + ".*")) {
                lvmList.add(pairs.getValue());
            }
        }
        return lvmList;
    }
    
    private MBeanServerConnection connectToMBeanServer(LocalVirtualMachine lvm) throws IOException {
        logger.debug("connecting to local jvm: id:" + lvm.vmid());

        JMXServiceURL jmxUrl = null;
        if (lvm != null) {
            if (!lvm.isManageable()) {
                lvm.startManagementAgent();
                if (!lvm.isManageable()) {
                    throw new IOException(lvm + " not manageable");
                }
            }
            jmxUrl = new JMXServiceURL(lvm.connectorAddress());
        }
        // get server
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
        // connect
        MBeanServerConnection mbs = jmxc.getMBeanServerConnection();
        return mbs;
    }
    
    List<MBeanAttributeData> getMBeansAttributesData(LocalVirtualMachine lvm, String nameRegexp) 
    throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
        logger.debug("-- get list of mbeans names - attributes --------------------------");

        long jvmId = lvm.vmid();
        String jvmName = lvm.displayName();
//        logger.debug("connecting to local jvm: " + jvmId + ":" + jvmName);
        
        nameRegexp = ".*" + nameRegexp + ".*";

        // result collection
        ArrayList<MBeanAttributeData> attributeDataList = new ArrayList<MBeanAttributeData>();
        
        MBeanServerConnection mbs = connectToMBeanServer(lvm);
        
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
    
    /*
    class LocalVirtualMachine2 {
        
        // This method returns the list of all virtual machines currently
        // running on the machine
        public Map<Integer, LocalVirtualMachine> getAllVirtualMachines() {
            Map<Integer, LocalVirtualMachine> map =
                new HashMap<Integer, LocalVirtualMachine>();
            getMonitoredVMs(map);
            getAttachableVMs(map);
            return map;
        }

        private void getMonitoredVMs(Map<Integer, LocalVirtualMachine> map) {
            MonitoredHost host;
            Set vms;
            try {
                host = MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
                vms = host.activeVms();
            } catch (java.net.URISyntaxException sx) {
                throw new InternalError(sx.getMessage());
            } catch (MonitorException mx) {
                throw new InternalError(mx.getMessage());
            }
            for (Object vmid: vms) {
                if (vmid instanceof Integer) {
                    int pid = ((Integer) vmid).intValue();
                    String name = vmid.toString(); // default to pid if name not available
                    boolean attachable = false;
                    String address = null;
                    try {
                         MonitoredVm mvm = host.getMonitoredVm(new VmIdentifier(name));
                         // use the command line as the display name
                         name =  MonitoredVmUtil.commandLine(mvm);
                         attachable = MonitoredVmUtil.isAttachable(mvm);
                         address = ConnectorAddressLink.importFrom(pid);
                         mvm.detach();
                    } catch (Exception x) {
                         // ignore
                    }
                    map.put((Integer) vmid,
                            new LocalVirtualMachine(pid, name, attachable, address));
                }
            }
        }
        
        private static final String LOCAL_CONNECTOR_ADDRESS_PROP =
            "com.sun.management.jmxremote.localConnectorAddress";
        
        private void getAttachableVMs(Map<Integer, LocalVirtualMachine> map) {
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : vms) {
                try {
                    Integer vmid = Integer.valueOf(vmd.id());
                    if (!map.containsKey(vmid)) {
                        boolean attachable = false;
                        String address = null;
                        try {
                            VirtualMachine vm = VirtualMachine.attach(vmd);
                            attachable = true;
                            Properties agentProps = vm.getAgentProperties();
                            address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
                            vm.detach();
                        } catch (AttachNotSupportedException x) {
                            // not attachable
                        } catch (IOException x) {
                            // ignore
                        }
                        map.put(vmid, new LocalVirtualMachine(vmid.intValue(),
                                                              vmd.displayName(),
                                                              attachable,
                                                              address));
                    }
                } catch (NumberFormatException e) {
                    // do not support vmid different than pid
                }
            }
        }
        
    }
    */
    
    public class MBeanAttributeData {
        
        private long jvmId;
        private String jvmName;
        private String domainName;
        private String mbeanName;
        private String mbeanAttributeName;
        private double value = 0;
        
        MBeanAttributeData(long jvmId, String jvmName, String domainName, String mbeanName, String mbeanAttributeName) {
            this.jvmId = jvmId;
            this.jvmName = jvmName;
            this.domainName = domainName;
            this.mbeanName = mbeanName;
            this.mbeanAttributeName = mbeanAttributeName;
        }
        
        public String toString() {
            return domainName + ":" + mbeanName + ":" + mbeanAttributeName;
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
//            logger.debug("   > " + mbeanAttributeName + " : " + attribute);
            value = Double.parseDouble(attribute.toString());
        }
        
        void setNumberValue(Boolean attribute) {
//            logger.debug("   > " + mbeanAttributeName + " : " + attribute);
            value = "true".equals(attribute.toString())?1:0;
        }

        public long getJvmId() {
            return jvmId;
        }

        public String getJvmName() {
            return jvmName;
        }
        
        public String getDomainName() {
            return domainName;
        }

        public String getMbeanName() {
            return mbeanName;
        }

        public String getMbeanAttributeName() {
            return mbeanAttributeName;
        }

        public double getValue() {
            return value;
        }
        
    }
    
}
