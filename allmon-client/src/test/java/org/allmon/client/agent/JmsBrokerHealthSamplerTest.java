package org.allmon.client.agent;

import junit.framework.TestCase;

public class JmsBrokerHealthSamplerTest extends TestCase {

    public void testSamplingIsUp() {
        boolean isUp = JmsBrokerHealthSampler.getInstance().isJmsBrokerUp();
        assertTrue(isUp);
    }
    
    public void testSamplingEveryMinute() throws InterruptedException {
        boolean isUp = JmsBrokerHealthSampler.getInstance().isJmsBrokerUp();
        long sampleTime0 = JmsBrokerHealthSampler.getInstance().getLastCheckTime();
        assertTrue(isUp);
        Thread.sleep(150000);
        long sampleTime1 = JmsBrokerHealthSampler.getInstance().getLastCheckTime();
        assertTrue("Sample has to be taken every 60 sec", sampleTime1 - sampleTime0 <= 61000);
    }
    
}
