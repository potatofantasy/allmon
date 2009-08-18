package org.allmon.client.agent;

import org.allmon.client.aggregator.MetricMessageWrapper;

import junit.framework.TestCase;

public class MetricMessageWrapperTest extends TestCase {

    public void testAdd() throws Exception {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        metricMessageWrapper.add(MetricMessageFactory.createClassMessage("class1", "met1", "usr1", 1));
        metricMessageWrapper.add(MetricMessageFactory.createClassMessage("class2", "met1", "usr1", 1));
        assertEquals(2, metricMessageWrapper.size());
    }

    public void testAddWrapper() throws Exception {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        metricMessageWrapper.add(MetricMessageFactory.createClassMessage("class1", "met1", "usr1", 1));
        assertEquals(1, metricMessageWrapper.size());

        MetricMessageWrapper metricMessageWrapper2 = new MetricMessageWrapper();
        metricMessageWrapper2.add(MetricMessageFactory.createClassMessage("class1", "met1", "usr1", 1));
        assertEquals(1, metricMessageWrapper2.size());

        metricMessageWrapper.add(metricMessageWrapper2);
        assertEquals(2, metricMessageWrapper.size());
        assertEquals(1, metricMessageWrapper2.size());
    }

}
