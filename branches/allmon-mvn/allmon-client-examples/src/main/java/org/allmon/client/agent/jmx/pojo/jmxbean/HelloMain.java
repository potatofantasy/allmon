package org.allmon.client.agent.jmx.pojo.jmxbean;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Main.java - main class for Hello World example. Create the HelloWorld MBean,
 * register it, then wait forever (or until the program is interrupted).
 * 
 * java -Dcom.sun.management.jmxremote org.allmon.client.agent.jmxmbean.HelloMain
 * 
 * for remote access: 
 * java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 org.allmon.client.agent.jmxmbean.HelloMain
 * 
 */
public class HelloMain {
    
    /**
     * For simplicity, we declare "throws Exception". Real programs will usually
     * want finer-grained exception handling.
     */
    public static void main(String[] args) throws Exception {
        // Get the Platform MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Construct the ObjectName for the MBean we will register
        ObjectName name = new ObjectName("org.allmon.client.agent.jmxmbean:type=Hello");

        // Create the Hello World MBean
        Hello mbean = new Hello();

        // Register the Hello World MBean
        mbs.registerMBean(mbean, name);

        // Wait forever
        System.out.println("Waiting forever...");
        Thread.sleep(Long.MAX_VALUE);
    }
}
