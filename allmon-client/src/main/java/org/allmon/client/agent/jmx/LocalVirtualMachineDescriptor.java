package org.allmon.client.agent.jmx;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

class LocalVirtualMachineDescriptor {

    private String address;
    private String commandLine;
    private int vmid;
    private boolean isAttachSupported;

    private static final String JMXREMOTE_PROP = "com.sun.management.jmxremote";
    static final String JMXREMOTE_LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    private static final String PROPERTIES_JAVA_HOME = "java.home";

    private static final String PATH_MANAGEMENT_AGENT_JAR = "/lib/management-agent.jar";

    LocalVirtualMachineDescriptor(int vmid, String commandLine, boolean canAttach, String connectorAddress) {
        this.vmid = vmid;
        this.commandLine = commandLine;
        this.address = connectorAddress;
        this.isAttachSupported = canAttach;
    }

    static LocalVirtualMachineDescriptor createDescriptor(MonitoredHost host, Integer vmid) {
        int pid = vmid.intValue();
        String name = vmid.toString(); // default to pid if name not available
        boolean attachable = false;
        String address = null;
        MonitoredVm mvm = null;
        try {
            mvm = host.getMonitoredVm(new VmIdentifier(vmid.toString()));
            name = MonitoredVmUtil.commandLine(mvm); // use the command line as the display name
            attachable = MonitoredVmUtil.isAttachable(mvm);
            //address = sun.management.ConnectorAddressLink.importFrom(pid); // FIXME remove sun.management dependencies
        } catch (Exception x) {
        } finally {
            if (mvm != null) {
                try {
                    mvm.detach();
                } catch (Exception x) {
                }
            }
        }
        return new LocalVirtualMachineDescriptor(pid, name, attachable, address);
    }

    static LocalVirtualMachineDescriptor createDescriptor(VirtualMachineDescriptor vmd) {
        Integer vmid = Integer.valueOf(vmd.id());
        boolean attachable = false;
        String address = null;
        try {
            VirtualMachine vm = VirtualMachine.attach(vmd);
            attachable = true;
            Properties agentProps = vm.getAgentProperties();
            address = (String) agentProps.get(LocalVirtualMachineDescriptor.JMXREMOTE_LOCAL_CONNECTOR_ADDRESS_PROP);
            vm.detach();
        } catch (AttachNotSupportedException x) {
            // attachable = false;
        } catch (IOException x) {
        }
        return new LocalVirtualMachineDescriptor(vmid.intValue(), vmd.displayName(), attachable, address);
    }

    public int getVMid() {
        return vmid;
    }

    public boolean isManageable() {
        return address != null;
    }

    public boolean isAttachable() {
        return isAttachSupported;
    }

    /**
     * The method returns address of virtual machine instance. Can return null
     * if VM not available or no JMX agent.
     */
    public String connectorAddress() {
        return address;
    }

    public String toString() {
        return commandLine;
    }

    public String getCannonicalName() {
        return commandLine + ":" + "id=" + vmid;
    }

    public void startManagementAgent() throws IOException {
        if (address != null) {
            return; // the agent already started
        }

        if (!isAttachable()) {
            throw new IOException("This virtual machine, id=" + vmid + " does not support dynamic attach");
        }

        // main loading method
        loadManagementAgent();

        // extra check - when fails to load or start the management agent
        if (address == null) {
            throw new IOException("Connector address after succesfull loading cannot be found");
        }
    }

    /**
     * Load the management agent into the target VM
     * 
     * @throws IOException
     */
    private void loadManagementAgent() throws IOException {
        VirtualMachine vm = loadVM();
        File managementJar = loadManagementJar(vm);
        String agent = managementJar.getCanonicalPath();
        try {
            vm.loadAgent(agent, JMXREMOTE_PROP);
        } catch (AgentLoadException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        } catch (AgentInitializationException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        }
        // get the connector address
        Properties agentProps = vm.getAgentProperties();
        address = (String) agentProps.get(JMXREMOTE_LOCAL_CONNECTOR_ADDRESS_PROP);
        vm.detach();
    }

    private VirtualMachine loadVM() throws IOException {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(String.valueOf(vmid));
        } catch (AttachNotSupportedException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        }
        return vm;
    }
    
    private File loadManagementJar(VirtualMachine vm) throws IOException {
        String home = vm.getSystemProperties().getProperty(PROPERTIES_JAVA_HOME);
        // management-agent.jar can be in JAVA_HOME/jre/lib/ but also can be in JAVA_HOME/lib
        File f = new File(home + "/jre" + PATH_MANAGEMENT_AGENT_JAR);
        if (!f.exists()) {
            f = new File(home + PATH_MANAGEMENT_AGENT_JAR);
            if (!f.exists()) {
                throw new IOException("Management agent not found");
            }
        }
        return f;
    }

}