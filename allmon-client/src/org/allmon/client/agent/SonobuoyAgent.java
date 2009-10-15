package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agents sends ping messages (signals) only.
 */
public class SonobuoyAgent extends ActiveAgent  {

    private static final Log logger = LogFactory.getLog(SonobuoyAgent.class);
    
    MetricMessage collectMetrics() {
        MetricMessage metricMessage = MetricMessageFactory.createPingMessage();
        return metricMessage;
    }

    public void setParameters(String[] paramsString) {
        // TODO Auto-generated method stub
        
    }
    
}
