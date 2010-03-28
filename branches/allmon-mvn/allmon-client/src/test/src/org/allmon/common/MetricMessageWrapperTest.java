package org.allmon.common;

import junit.framework.TestCase;

public class MetricMessageWrapperTest extends TestCase {

    public void testAdd() throws Exception {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        metricMessageWrapper.add(MetricMessageFactory.createClassMessage("className1", "method1", "classNameX", "methodNameX", 1));
        metricMessageWrapper.add(MetricMessageFactory.createClassMessage("className2", "method1", "classNameX", "methodNameX", 1));
        assertEquals(2, metricMessageWrapper.size());
    }

    public void testAddWrapper() throws Exception {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        metricMessageWrapper.add(MetricMessageFactory.createClassMessage("className1", "method1", "classNameX", "methodNameX", 1));
        assertEquals(1, metricMessageWrapper.size());

        MetricMessageWrapper metricMessageWrapper2 = new MetricMessageWrapper();
        metricMessageWrapper2.add(MetricMessageFactory.createClassMessage("className1", "method1", "classNameX", "methodNameX", 1));
        assertEquals(1, metricMessageWrapper2.size());

        metricMessageWrapper.add(metricMessageWrapper2);
        assertEquals(2, metricMessageWrapper.size());
        assertEquals(1, metricMessageWrapper2.size());
    }

}
