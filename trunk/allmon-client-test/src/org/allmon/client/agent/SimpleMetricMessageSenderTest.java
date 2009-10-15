package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;

public class SimpleMetricMessageSenderTest extends TestCase {

    public void testInsertEntryAndExitPoint() throws Exception {
        MetricMessage metricMessage = MetricMessageFactory4Test.createClassMessage("className", "methodName", "user", 1);
        SimpleMetricMessageSender sender = new SimpleMetricMessageSender(metricMessage);
        sender.insertEntryPoint();
        Thread.sleep(1234);
        sender.insertExitPoint();
    }
    
}
