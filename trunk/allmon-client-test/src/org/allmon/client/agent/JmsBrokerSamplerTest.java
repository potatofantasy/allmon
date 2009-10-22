package org.allmon.client.agent;

import junit.framework.TestCase;

public class JmsBrokerSamplerTest extends TestCase {

    public void testSamplingIsUp() {
        boolean isUp = JmsBrokerSampler.getInstance().isJmsBrokerUp();
        assertTrue(isUp);
    }
    
    public void testSamplingEveryMinute() throws InterruptedException {
        boolean isUp = JmsBrokerSampler.getInstance().isJmsBrokerUp();
        long sampleTime0 = JmsBrokerSampler.getInstance().getLastCheckTime();
        assertTrue(isUp);
        Thread.sleep(150000);
        long sampleTime1 = JmsBrokerSampler.getInstance().getLastCheckTime();
        assertTrue("Sample has to be taken every 60 sec", sampleTime1 - sampleTime0 <= 61000);
    }
    
}
