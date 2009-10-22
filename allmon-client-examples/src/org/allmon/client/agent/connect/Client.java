package org.allmon.client.agent.connect;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
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
public class Client {

	public static void main(String[] args) {
		try {
			// Create an RMI connector client and
			// connect it to the RMI connector server
			//
			echo("\nCreate an RMI connector client and connect it to the RMI connector server");
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://localhost:9999/server");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

			// Create listener
			//
			ClientListener listener = new ClientListener();

			// Get an MBeanServerConnection
			//
			echo("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			waitForEnterPressed();

			// Get domains from MBeanServer
			//
			echo("\nDomains:");
			String domains[] = mbsc.getDomains();
			for (int i = 0; i < domains.length; i++) {
				echo("\tDomain[" + i + "] = " + domains[i]);
			}
			waitForEnterPressed();

			// Get MBeanServer's default domain
			//
			String domain = mbsc.getDefaultDomain();

			// Create SimpleStandard MBean
			//
			ObjectName stdMBeanName = new ObjectName(domain
					+ ":type=SimpleStandard,index=2");
			echo("\nCreate SimpleStandard MBean...");
			mbsc.createMBean("SimpleStandard", stdMBeanName, null, null);
			waitForEnterPressed();

			// Create SimpleDynamic MBean
			//
			ObjectName dynMBeanName = new ObjectName(domain
					+ ":type=SimpleDynamic,index=2");
			echo("\nCreate SimpleDynamic MBean...");
			mbsc.createMBean("SimpleDynamic", dynMBeanName, null, null);
			waitForEnterPressed();

			// Get MBean count
			//
			echo("\nMBean count = " + mbsc.getMBeanCount());

			// Query MBean names
			//
			echo("\nQuery MBeanServer MBeans:");
			Set names = mbsc.queryNames(null, null);
			for (Iterator i = names.iterator(); i.hasNext();) {
				echo("\tObjectName = " + (ObjectName) i.next());
			}
			waitForEnterPressed();

			// -------------------------------
			// Manage the SimpleStandard MBean
			// -------------------------------
			echo("\n>>> Perform operations on SimpleStandard MBean <<<");

			// Get State attribute in SimpleStandard MBean
			//
			echo("\nState = " + mbsc.getAttribute(stdMBeanName, "State"));

			// Set State attribute in SimpleStandard MBean
			//
			mbsc.setAttribute(stdMBeanName, new Attribute("State",
					"changed state"));

			// Get State attribute in SimpleStandard MBean
			//
			// Another way of interacting with a given MBean is through a
			// dedicated proxy instead of going directly through the MBean
			// server connection
			//
			SimpleStandardMBean proxy = (SimpleStandardMBean) MBeanServerInvocationHandler
					.newProxyInstance(mbsc, stdMBeanName,
							SimpleStandardMBean.class, true);
			echo("\nState = " + proxy.getState());

			// Add notification listener on SimpleStandard MBean
			//
			echo("\nAdd notification listener...");
			mbsc.addNotificationListener(stdMBeanName, listener, null, null);

			// Invoke "reset" in SimpleStandard MBean
			//
			// Calling "reset" makes the SimpleStandard MBean emit a
			// notification that will be received by the registered
			// ClientListener.
			//
			echo("\nInvoke reset() in SimpleStandard MBean...");
			mbsc.invoke(stdMBeanName, "reset", null, null);

			// Sleep for 2 seconds in order to have time to receive the
			// notification before removing the notification listener.
			//
			echo("\nWaiting for notification...");
			sleep(2000);

			// Remove notification listener on SimpleStandard MBean
			//
			echo("\nRemove notification listener...");
			mbsc.removeNotificationListener(stdMBeanName, listener);

			// Unregister SimpleStandard MBean
			//
			echo("\nUnregister SimpleStandard MBean...");
			mbsc.unregisterMBean(stdMBeanName);
			waitForEnterPressed();

			// ------------------------------
			// Manage the SimpleDynamic MBean
			// ------------------------------
			echo("\n>>> Perform operations on SimpleDynamic MBean <<<");

			// Get State attribute in SimpleDynamic MBean
			//
			echo("\nState = " + mbsc.getAttribute(dynMBeanName, "State"));

			// Set State attribute in SimpleDynamic MBean
			//
			mbsc.setAttribute(dynMBeanName, new Attribute("State",
					"changed state"));

			// Get State attribute in SimpleDynamic MBean
			//
			echo("\nState = " + mbsc.getAttribute(dynMBeanName, "State"));

			// Add notification listener on SimpleDynamic MBean
			//
			echo("\nAdd notification listener...");
			mbsc.addNotificationListener(dynMBeanName, listener, null, null);

			// Invoke "reset" in SimpleDynamic MBean
			//
			// Calling "reset" makes the SimpleDynamic MBean emit a
			// notification that will be received by the registered
			// ClientListener.
			//
			echo("\nInvoke reset() in SimpleDynamic MBean...");
			mbsc.invoke(dynMBeanName, "reset", null, null);

			// Sleep for 2 seconds in order to have time to receive the
			// notification before removing the notification listener.
			//
			echo("\nWaiting for notification...");
			sleep(2000);

			// Remove notification listener on SimpleDynamic MBean
			//
			echo("\nRemove notification listener...");
			mbsc.removeNotificationListener(dynMBeanName, listener);

			// Unregister SimpleDynamic MBean
			//
			echo("\nUnregister SimpleDynamic MBean...");
			mbsc.unregisterMBean(dynMBeanName);
			waitForEnterPressed();

			// Close MBeanServer connection
			//
			echo("\nClose the connection to the server");
			jmxc.close();
			echo("\nBye! Bye!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void echo(String msg) {
		System.out.println(msg);
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void waitForEnterPressed() {
		try {
			echo("\nPress <Enter> to continue...");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
