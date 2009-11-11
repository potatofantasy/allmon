package org.allmon.client.agent.jmxmbean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import sun.tools.jconsole.LocalVirtualMachine;

public class ReadHelloMain {

	public static void main(String[] args) throws IOException, NullPointerException {

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
	        System.out.println(mbean);
        }
	    
	    System.out.println("- get list of mbeans (classes) instances --------------------------");
        Set<ObjectInstance> mbeanInstances = mbs.queryMBeans(null, null);
        for (ObjectInstance mbeanInstance : mbeanInstances) {
            System.out.println(mbeanInstance);
        }

        //"java.lang:type=Memory"
	    
//	    System.out.println("\nCreate an RMI connector client and connect it to the RMI connector server");
//        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
//        
//	    JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

	}

}
