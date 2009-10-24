package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

import junit.framework.TestCase;

/**
 * This test should be potentially in the future part of integration test suite, 
 * because is using active jms broker instance. Later taken messages should be 
 * transformed (aggregated) by allmon client and send to server to load the metrics 
 * to allmon database.
 */
public class MetricMessageSenderTest extends TestCase {

    public void testInsertEntryAndExitPoint() throws Exception {
    
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage("className", "methodName", "classNameX", "methodNameX", 1);
        
        MetricMessageSender sender = new MetricMessageSender(metricMessage) {
            public void insertEntryPoint() {
                sendEntryPoint();
            }
            public void insertExitPoint() {
                sendExitPoint(null);
            }
            public void insertExitPointException(Exception exception) {
                sendExitPoint(exception);
            }
        };
        
        sender.insertEntryPoint();
        
        sender.insertExitPoint();
        
    }    
    
}
