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
                    // active mq
                    //".*activemq.*",
                    //JDK pools an OS memory allocations, GC stats, thread count, class loading
                    //"(.*java.lang:type=Memory.*used.*)|(.*java.lang:type=OperatingSystem.*)|(.*java.lang:type=GarbageCollector.*)|(.*java.lang:type=Threading.*ThreadCount.*)|(.*java.lang:type=ClassLoading.*)",
                    //".*java.lang:type=Threading:CurrentThreadCpuTime" //"java.lang:type=Threading"
                    //".*java.lang:type=Runtime.*"
                    
                    // allmon
                    //".*allmon.*", //".*AgentAggregatorMain.*",
                    //"(.*java.lang:type=Memory.*used.*)|(.*java.lang:type=OperatingSystem.*)|(.*java.lang:type=GarbageCollector.*)|(.*java.lang:type=Threading.*ThreadCount.*)|(.*java.lang:type=ClassLoading.*)"
                    
                    //jboss specific
                    //".*jboss.*", // all jboss based metrics
                    //".*jboss.j2ee:service=EJB,plugin=pool,.*:CurrentSize.*", // ejb pools
                    //".*jboss.jca:service=CachedConnectionManager:InUseConnections.*",
                    //".*jboss.jca:service=JCAMetaDataRepository,name=DefaultJCAMetaDataRepository:ManagedConnectionFactoryCount.*",
                    //".*jboss.jca:service=ManagedConnectionPool,name=.*:.*Connection.*Count.*" //ex: ConnectionCount, InUseConnectionCount, ConnectionCreatedCount, ConnectionDestroyedCount
                    //".*jboss.messaging.destination:service=Queue,name=.*:.*", // ex: ConsumerCount, ScheduledMessageCount, RedeliveryDelay, FullSize, 
                    //".*jboss.system:type=ServerInfo.*", // ex: ActiveThreadCount, AvailableProcessors, FreeMemory, MaxMemory, TotalMemory
                    //".*jboss.web:j2eeType=Servlet,name=.*J2EEServer=.*Count.*" // ex: requestCount, errorCount
                    //".*jboss.web:j2eeType=Servlet,name=.*J2EEServer=.*Time.*" // ex: loadTime, processingTime, classLoadTime
                    //".*jboss.web:type=Cache,host=.*,path=.*", // ex: hitsCount, accessCount, cacheSize, spareNotFoundEntries, ...
                    //".*jboss.web:type=Manager,path=.*,host.*:activeSessions.*", // ex: activeSessions, expiredSessions, sessionCounter, rejectedSessions
                    //".*jboss:service=invoker,type=pooled:.*", // ex: CurrentClientPoolSize, CurrentThreadPoolSize, ClientRetryCount
                    //".*jboss:service=TransactionManager:.*Count.*", // ex: CommitCount, RunningTransactionCount, HeuristicCount, RollbackCount, TimedoutCount
                    ".*JMImplementation:type=MBeanRegistry:Size*", // ex: MBeanRegistry:Size
                    
                    
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