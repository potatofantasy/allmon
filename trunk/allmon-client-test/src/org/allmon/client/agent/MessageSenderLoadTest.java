package org.allmon.client.agent;

import org.allmon.common.AbstractLoadTest;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
public class MessageSenderLoadTest extends AbstractLoadTest<MessageSender, Object> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(MessageSenderLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 5; // TODO find out WHY above 5 for 500 calls - sending messages process hangs!!!
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 100;
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 400;
    // soak test - around 20min
//    private final static long THREADS_COUNT = 50;
//    private final static long STARTING_TIME_MILLIS = 1 * 60 * 1000; // rump-up 1 min
//    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 1000; // (!) 2 calls per1 sec
//    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1200 * (1000 / SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX);
    
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 10000);
    }
    
    public MessageSender initialize() {
        MessageSender messageSender = new MessageSender();
        return messageSender;
    }
    
    public Object preCall(int thread, int iteration, MessageSender messageSender) {
        MetricMessage metricMessage = MetricMessageFactory4Test.createClassMessage(
                "className" + iteration, "methodName", "classNameX", "methodNameX", 1);
        metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_ENTRY);
        messageSender.sendMessage(metricMessage);
        return null;
    }
    
    public void postCall(Object preCallParameters) {
    }
    
}
