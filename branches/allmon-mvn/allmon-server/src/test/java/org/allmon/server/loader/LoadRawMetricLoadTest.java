package org.allmon.server.loader;

import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.allmon.common.loadtest.AbstractLoadTest;

public class LoadRawMetricLoadTest extends AbstractLoadTest<LoadRawMetric, Object> {

    private final static long THREADS_COUNT = 1;
    private final static long STARTING_TIME_MILLIS = 10;
    
    private final static int METRICS_COUNT = 1000;

    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, STARTING_TIME_MILLIS, 1, 1, 1000);
    }

    public LoadRawMetric initialize() {
        return new LoadRawMetric();
    }

    public Object preCall(int thread, int iteration, LoadRawMetric loadRawMetric) {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        for (int i = 0; i < METRICS_COUNT; i++) {
            metricMessageWrapper.add(MetricMessageFactory.createClassMessage(
                            "classNameCalled", "methodNameCalled", "classNameCalling", "methodNameCalling"));
        }
        
        loadRawMetric.storeMetric(metricMessageWrapper);
        return metricMessageWrapper;
    }

    public void postCall(Object metrics) {
    }

}
