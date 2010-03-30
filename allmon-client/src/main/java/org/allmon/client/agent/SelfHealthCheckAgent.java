package org.allmon.client.agent;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;

import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelfHealthCheckAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(SelfHealthCheckAgent.class);

    public SelfHealthCheckAgent(AgentContext agentContext) {
        super(agentContext);
    }
    
    MetricMessageWrapper collectMetrics() {
        MetricMessageWrapper metricWrapper = new MetricMessageWrapper();
        metricWrapper.add(getMemoryMetrics());
        metricWrapper.add(getThreadsMetrics());
        return metricWrapper;
    }
    
    protected void decodeAgentTaskableParams() {
    }
    
    private final static List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
    private final static ThreadMXBean threads = ManagementFactory.getThreadMXBean();
    private final static List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
    
    public MetricMessageWrapper getMemoryMetrics() {
        MetricMessageWrapper messageWrapper = new MetricMessageWrapper();
        for (MemoryPoolMXBean mp : memoryPools) {
            String className = mp.getClass().getName();
            String name = mp.getName();
            long used = mp.getUsage().getUsed();
            long init = mp.getUsage().getInit();
            long committed= mp.getUsage().getCommitted();
            long total = mp.getUsage().getMax();
            
            logger.debug("memoryPool: " + name + ", used: " + used + ", init:" + init + ", committed:" + committed + ", " + total);
            
            messageWrapper.add(MetricMessageFactory.createJmxMessage(
                    "local", className + ":" + name, "used", used, null));
            messageWrapper.add(MetricMessageFactory.createJmxMessage(
                    "local", className + ":" + name, "init", init, null));
            messageWrapper.add(MetricMessageFactory.createJmxMessage(
                    "local", className + ":" + name, "committed", committed, null));
            messageWrapper.add(MetricMessageFactory.createJmxMessage(
                    "local", className + ":" + name, "total", total, null));
        }
        return messageWrapper;
    }
    
    public MetricMessageWrapper getThreadsMetrics() {
        MetricMessageWrapper messageWrapper = new MetricMessageWrapper();
        
        String threadClassName = threads.getClass().getName();
        String domainNameMBeanName = threadClassName + ":threadsAll";
        long totalStartedThreadCount = threads.getTotalStartedThreadCount();
        long daemonThreadCount = threads.getDaemonThreadCount();
        long threadCount = threads.getThreadCount();
        
        messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                domainNameMBeanName, "totalStartedThreadCount", totalStartedThreadCount, null));
        messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                domainNameMBeanName, "daemonThreadCount", daemonThreadCount, null));
        messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                domainNameMBeanName, "threadCount", threadCount, null));
        
        //long[] threadIds = threads.getAllThreadIds();
        ThreadInfo [] threadInfos = threads.getThreadInfo(threads.getAllThreadIds());
        
        for (ThreadInfo threadInfo : threadInfos) {
            //String name = t.getName();
            String threadName = threadInfo.getThreadName();
            
            long blockedCount = threadInfo.getBlockedCount();
            long blockedTime = threadInfo.getBlockedTime();
            long waitedCount = threadInfo.getWaitedCount();
            long waitedTime = threadInfo.getWaitedTime();
            
            logger.debug("threadName: " + threadName + ", blockedCount: " + blockedCount + ", blockedTime:" + blockedTime + ", waitedCount:" + waitedCount + ", waitedTime : " + waitedTime);
            
            messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                    threadClassName + ":" + threadName, "blockedCount", blockedCount, null));
            messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                    threadClassName + ":" + threadName, "blockedTime", blockedTime, null));
            messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                    threadClassName + ":" + threadName, "waitedCount", waitedCount, null));
            messageWrapper.add(MetricMessageFactory.createJmxMessageAgentVM(
                    threadClassName + ":" + threadName, "waitedTime", waitedTime, null));
        }
        return messageWrapper;
    }
    
}
