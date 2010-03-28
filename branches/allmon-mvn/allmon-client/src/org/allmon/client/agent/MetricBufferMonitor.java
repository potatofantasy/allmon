package org.allmon.client.agent;


public class MetricBufferMonitor {
    
    private final MonitoringThread monitoringThread = new MonitoringThread();
    
    
    MetricBufferMonitor() {
        monitoringThread.start();
    }
    
    private class MonitoringThread extends Thread {
        
        
        
    }
    
}
