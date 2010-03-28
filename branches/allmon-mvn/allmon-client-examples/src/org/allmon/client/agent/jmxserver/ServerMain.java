package org.allmon.client.agent.jmxserver;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * 
# Compile Java classes
#
# * Server.java: creates an MBeanServer,
#                registers a SimpleStandard MBean on the local MBeanServer,
#                registers a SimpleDynamic MBean on the local MBeanServer,
#                performs local operations on both MBeans,
#                creates and starts an RMI connector server (JRMP).
#
# * Client.java: creates an RMI connector (JRMP),
#                registers a SimpleStandard MBean on the remote MBeanServer,
#                registers a SimpleDynamic MBean on the remote MBeanServer,
#                performs remote operations on both MBeans,
#                closes the RMI connector.
#
# * ClientListener.java: implements a generic notification listener.
#
# * SimpleStandard.java: implements the Simple standard MBean.
#
# * SimpleStandardMBean.java: the management interface exposed
#                             by the Simple standard MBean.
#
# * SimpleDynamic.java: implements the Simple dynamic MBean.
#
 */
public class ServerMain {

	public static void main(String[] args) {
		try {
		    // Print all domains in PlatformMBeanServer
            System.out.println("\n>>> Print all domains in PlatformMBeanServer");
		    MBeanServer pmbs = ManagementFactory.getPlatformMBeanServer();
		    String[] domains = pmbs.getDomains();
		    System.out.println("\t " + Arrays.toString(domains));
            waitForEnterPressed();
		    
		    // Instantiate the MBean server
			//
			System.out.println("\n>>> Create the MBean server");
			MBeanServer mbs = MBeanServerFactory.createMBeanServer();
			waitForEnterPressed();

			// Get default domain
			//
			System.out.println("\n>>> Get the MBean server's default domain");
			String domain = mbs.getDefaultDomain();
			System.out.println("\tDefault Domain = " + domain);
			waitForEnterPressed();

			// Create and register the SimpleStandard MBean
			//
			String mbeanClassName = "org.allmon.client.agent.jmxserver.SimpleStandard";
			String mbeanObjectNameStr = domain + ":type=" + mbeanClassName + ",index=1";
			ObjectName mbeanObjectName = createSimpleMBean(mbs, mbeanClassName, mbeanObjectNameStr);
			waitForEnterPressed();

			// Get and display the management information exposed by the
			// SimpleStandard MBean
			//
			printMBeanInfo(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			// Manage the SimpleStandard MBean
			// 
			manageSimpleMBean(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			// Create and register the SimpleDynamic MBean
			//
			mbeanClassName = "org.allmon.client.agent.jmxserver.SimpleDynamic";
			mbeanObjectNameStr = domain + ":type=" + mbeanClassName + ",index=1";
			mbeanObjectName = createSimpleMBean(mbs, mbeanClassName, mbeanObjectNameStr);
			waitForEnterPressed();

			// Get and display the management information exposed by the
			// SimpleDynamic MBean
			//
			printMBeanInfo(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			// Manage the SimpleDynamic MBean
			// 
			manageSimpleMBean(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			// Create an RMI connector server
			//
			System.out.println("\nCreate an RMI connector server");
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
			JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);

			// Start the RMI connector server
			//
			System.out.println("\nStart the RMI connector server");
			cs.start();
			System.out.println("\nThe RMI connector server successfully started");
			System.out.println("and is ready to handle incoming connections");
			System.out.println("\nStart the client on a different window and");
			System.out.println("press <Enter> once the client has finished");
			waitForEnterPressed();

			// Stop the RMI connector server
			//
			System.out.println("\nStop the RMI connector server");
			cs.stop();
			System.out.println("\nBye! Bye!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ObjectName createSimpleMBean(MBeanServer mbs,
			String mbeanClassName, String mbeanObjectNameStr) {
		System.out.println("\n>>> Create the " + mbeanClassName + " MBean within the MBeanServer");
		System.out.println("\tObjectName = " + mbeanObjectNameStr);
		try {
			ObjectName mbeanObjectName = ObjectName.getInstance(mbeanObjectNameStr);
			mbs.createMBean(mbeanClassName, mbeanObjectName);
			return mbeanObjectName;
		} catch (Exception e) {
			System.out.println("\t!!! Could not create the " + mbeanClassName + " MBean !!!");
			e.printStackTrace();
			System.out.println("\nEXITING...\n");
			System.exit(1);
		}
		return null;
	}

	public static void printMBeanInfo(MBeanServer mbs,
			ObjectName mbeanObjectName, String mbeanClassName) {
		System.out.println("\n>>> Retrieve the management information for the " + mbeanClassName);
		System.out.println("    MBean using the getMBeanInfo() method of the MBeanServer");
		MBeanInfo info = null;
		try {
			info = mbs.getMBeanInfo(mbeanObjectName);
		} catch (Exception e) {
			System.out.println("\t!!! Could not get MBeanInfo object for " + mbeanClassName + " !!!");
			e.printStackTrace();
			return;
		}
		System.out.println("\nCLASSNAME: \t" + info.getClassName());
		System.out.println("\nDESCRIPTION: \t" + info.getDescription());
		System.out.println("\nATTRIBUTES");
		MBeanAttributeInfo[] attrInfo = info.getAttributes();
		if (attrInfo.length > 0) {
			for (int i = 0; i < attrInfo.length; i++) {
				System.out.println(" ** NAME: \t" + attrInfo[i].getName());
				System.out.println("    DESCR: \t" + attrInfo[i].getDescription());
				System.out.println("    TYPE: \t" + attrInfo[i].getType() + "\tREAD: " + attrInfo[i].isReadable() + "\tWRITE: " + attrInfo[i].isWritable());
			}
		} else {
			System.out.println(" ** No attributes **");
		}
		System.out.println("\nCONSTRUCTORS");
		MBeanConstructorInfo[] constrInfo = info.getConstructors();
		for (int i = 0; i < constrInfo.length; i++) {
			System.out.println(" ** NAME: \t" + constrInfo[i].getName());
			System.out.println("    DESCR: \t" + constrInfo[i].getDescription());
			System.out.println("    PARAM: \t" + constrInfo[i].getSignature().length + " parameter(s)");
		}
		System.out.println("\nOPERATIONS");
		MBeanOperationInfo[] opInfo = info.getOperations();
		if (opInfo.length > 0) {
			for (int i = 0; i < opInfo.length; i++) {
				System.out.println(" ** NAME: \t" + opInfo[i].getName());
				System.out.println("    DESCR: \t" + opInfo[i].getDescription());
				System.out.println("    PARAM: \t" + opInfo[i].getSignature().length + " parameter(s)");
			}
		} else {
			System.out.println(" ** No operations ** ");
		}
		System.out.println("\nNOTIFICATIONS");
		MBeanNotificationInfo[] notifInfo = info.getNotifications();
		if (notifInfo.length > 0) {
			for (int i = 0; i < notifInfo.length; i++) {
				System.out.println(" ** NAME: \t" + notifInfo[i].getName());
				System.out.println("    DESCR: \t" + notifInfo[i].getDescription());
				String notifTypes[] = notifInfo[i].getNotifTypes();
				for (int j = 0; j < notifTypes.length; j++) {
					System.out.println("    TYPE: \t" + notifTypes[j]);
				}
			}
		} else {
			System.out.println(" ** No notifications **");
		}
	}

	private static void manageSimpleMBean(MBeanServer mbs, ObjectName mbeanObjectName, String mbeanClassName) {

		System.out.println("\n>>> Manage the " + mbeanClassName + " MBean using its attributes ");
		System.out.println("    and operations exposed for management");

		try {
			// Get attribute values
			printSimpleAttributes(mbs, mbeanObjectName);

			// Change State attribute
			System.out.println("\n    Setting State attribute to value \"new state\"...");
			Attribute stateAttribute = new Attribute("State", "new state");
			mbs.setAttribute(mbeanObjectName, stateAttribute);

			// Get attribute values
			printSimpleAttributes(mbs, mbeanObjectName);

			// Invoking reset operation
			System.out.println("\n    Invoking reset operation...");
			mbs.invoke(mbeanObjectName, "reset", null, null);

			// Get attribute values
			printSimpleAttributes(mbs, mbeanObjectName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printSimpleAttributes(MBeanServer mbs,
			ObjectName mbeanObjectName) {
		try {
			System.out.println("\n    Getting attribute values:");
			String State = (String) mbs.getAttribute(mbeanObjectName, "State");
			Integer NbChanges = (Integer) mbs.getAttribute(mbeanObjectName, "NbChanges");
			System.out.println("\tState     = \"" + State + "\"");
			System.out.println("\tNbChanges = " + NbChanges);
		} catch (Exception e) {
			System.out.println("\t!!! Could not read attributes !!!");
			e.printStackTrace();
		}
	}

	private static void waitForEnterPressed() {
		try {
			System.out.println("\nPress <Enter> to continue...");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
