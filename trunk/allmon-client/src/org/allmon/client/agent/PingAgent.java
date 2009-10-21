package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PingAgent extends ActiveAgent  {

    private static final Log logger = LogFactory.getLog(PingAgent.class);
    
    private String pingedHoust = "google.com";
    
    MetricMessage collectMetrics() {
        // TODO add logic
        // TODO use common functionality with ShellCallAgent and ...Parser
        
        MetricMessage metricMessage = MetricMessageFactory.createPingMessage(pingedHoust, 1);
        return metricMessage;
    }

    void decodeAgentTaskableParams() {
    }
    
}
