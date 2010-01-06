package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;

import junit.framework.TestCase;

public class JmxServerAgentTest extends TestCase {

    // # Enable the jconsole agent locally
    // -Dcom.sun.management.jmxremote
    // # Tell JBossAS to use the platform MBean server
    // -Djboss.platform.mbeanserver
    
    public void _testSending() {
        AgentContext agentContext = new AgentContext();
        try {
            JmxServerAgent agent = new JmxServerAgent(agentContext);
            agent.setParameters(new String[]{
                    ".*activemq.*", //".*jboss.*", //".*AgentAggregatorMain.*"
                    ".*java.lang:type=Threading.*ThreadCount.*", //".*EJB3.*", //".*java.lang:type=Memory.*", // all memory metrics
                    //"(.*java.lang:type=Memory.*used.*)|(.*java.lang:type=GarbageCollector.*)"
                    //"java.lang:"
                    //".*java.lang:type=Threading:CurrentThreadCpuTime" //"java.lang:type=Threading"
                    //".*java.lang:type=Runtime.*"
                    //".*java.lang:type=Compilation.*"
            });
            agent.execute(); // collects metrics and sent the metrics messages to the broker
        } finally {
            agentContext.stop();
        }
    }
    
    public void testCollecting() {
        AgentContext agentContext = new AgentContext();
        try {
            JmxServerAgent agent = new JmxServerAgent(agentContext);
            agent.setParameters(new String[]{
                    ".*activemq.*", //".*jboss.*", //".*AgentAggregatorMain.*",
                    //JDK pools an OS memory allocations, GC stats, thread count, class loading
                    "(.*java.lang:type=Memory.*used.*)|(.*java.lang:type=OperatingSystem.*)|(.*java.lang:type=GarbageCollector.*)|(.*java.lang:type=Threading.*ThreadCount.*)|(.*java.lang:type=ClassLoading.*)",
                    //".*jboss.system:type=ServerInfo.*",
                    //".*java.lang:type=Threading:CurrentThreadCpuTime" //"java.lang:type=Threading"
                    //".*java.lang:type=Runtime.*"
                    //java.lang:type=Compilation:
            });
            //agent.execute(); // would collect metrics and sent the metrics messages to the broker
            agent.decodeAgentTaskableParams();
            MetricMessageWrapper messageWrapper = agent.collectMetrics();
            assertTrue(messageWrapper.size() > 0);
            System.out.println(messageWrapper);
        } finally {
            agentContext.stop();
        }
    }
    
}