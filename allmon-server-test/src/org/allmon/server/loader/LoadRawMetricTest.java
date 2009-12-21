package org.allmon.server.loader;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;

public class LoadRawMetricTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    public void testStoreMetrics() {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper(
                MetricMessageFactory.createClassMessage(
                        "classNameCalled", "methodNameCalled", "classNameCalling", "methodNameCalling", 0));

        LoadRawMetric loadRawMetric = new LoadRawMetric();
        loadRawMetric.storeMetric(metricMessageWrapper);
    }

    public void testLoadAllMetrics() {
        LoadRawMetric loadRawMetric = new LoadRawMetric();
        loadRawMetric.loadAllmetric();
    }

}