package org.allmon.client.agent.jmx;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * This class is used to build the list of all virtual machines 
 * which are running on the machine in a moment of execution the class constructor.
 * 
 */
public class LocalVirtualMachineManager {

    private Map<Integer, LocalVirtualMachineDescriptor> vmMap = new HashMap<Integer, LocalVirtualMachineDescriptor>();
    private MonitoredHost host;
    
    public LocalVirtualMachineManager() {
        getMonitoredVirtualMachines();
        getAttachableVirtualMachines();
    }
    
    public Map<Integer, LocalVirtualMachineDescriptor> getVirtualMachines() {
        return vmMap;
    }
    
    private void getMonitoredVirtualMachines() {
        Set<Integer> vms;
        try {
            host = MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
            vms = host.activeVms();
        } catch (URISyntaxException ex) {
            throw new InternalError(ex.getMessage());
        } catch (MonitorException ex) {
            throw new InternalError(ex.getMessage());
        }
        // goes through all active monitored VMs on the host and fills up VM map
        for (Integer vmid: vms) {
            vmMap.put(vmid, LocalVirtualMachineDescriptor.createDescriptor(host, vmid));
        }
    }
    
    private void getAttachableVirtualMachines() {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        // goes through all attachable VMs on the host and fills up VM map
        for (VirtualMachineDescriptor vmd : vms) {
            try {
                Integer vmid = Integer.valueOf(vmd.id());
                // id can be parsed 
                if (!vmMap.containsKey(vmid)) {
                    vmMap.put(vmid, LocalVirtualMachineDescriptor.createDescriptor(vmd));
                }
            } catch (NumberFormatException e) {
            }
        }
    }
    
}
