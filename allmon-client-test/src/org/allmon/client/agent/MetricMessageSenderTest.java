package org.allmon.client.agent;

import org.allmon.common.MetricMessage;

import junit.framework.TestCase;

public class MetricMessageSenderTest extends TestCase {

    public void testInsertEntryAndExitPoint() throws Exception {
    
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage("className", "methodName", "user", 1);
        
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
        
        for (int i = 0; i < 2; i++) {
            sender.insertEntryPoint();
        }
        
        for (int i = 0; i < 2; i++) {
            sender.insertExitPoint();
        }
        
    }    
    
}
