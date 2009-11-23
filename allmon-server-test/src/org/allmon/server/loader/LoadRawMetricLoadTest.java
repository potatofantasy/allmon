package org.allmon.server.loader;

import org.allmon.common.AbstractLoadTest;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;

public class LoadRawMetricLoadTest extends AbstractLoadTest<LoadRawMetric, Object> {

    private final static long THREADS_COUNT = 15;
    private final static long STARTING_TIME_MILLIS = 10;

    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, STARTING_TIME_MILLIS, 1, 2000, 10000);
    }

    public LoadRawMetric initialize() {
        return new LoadRawMetric();
    }

    public Object preCall(int thread, int iteration, LoadRawMetric loadRawMetric) {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper(
                MetricMessageFactory.createClassMessage(
                        "classNameCalled", "methodNameCalled", "classNameCalling", "methodNameCalling", 0));
        loadRawMetric.storeMetric(metricMessageWrapper);
        return metricMessageWrapper;
    }

    public void postCall(Object agent) {
    }

}
