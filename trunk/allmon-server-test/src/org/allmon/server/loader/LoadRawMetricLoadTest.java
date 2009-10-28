package org.allmon.server.loader;

import org.allmon.common.AbstractLoadTest;

public class LoadRawMetricLoadTest extends AbstractLoadTest<LoadRawMetric, Object> {

    private final static long THREADS_COUNT = 15;
    private final static long STARTING_TIME_MILLIS = 10;

    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, 1, 
                2000, 10000);
    }
    
    public LoadRawMetric initialize() {
        return new LoadRawMetric();
    }
    
    public Object preCall(int iteration, LoadRawMetric loadRawMetric) {
        RawMetric metric = new RawMetric();
        String str = ">" + Math.random() + ">" + Math.random() + ">" + Math.random() + ">" + Math.random();
        metric.setMetric(str);
        loadRawMetric.storeMetric(metric);
        return null;
    }
    
    public void postCall(Object agent) {
    }
    	
}
