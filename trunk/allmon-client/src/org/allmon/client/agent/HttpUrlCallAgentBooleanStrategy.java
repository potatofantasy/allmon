package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;

public class HttpUrlCallAgentBooleanStrategy extends HttpUrlCallAgentAbstractStrategy {
    
    MetricMessageWrapper extractMetrics() {
        String foundPhrase = OutputParser.findFirst(brCallResponse, agent.searchPhrase);
        
        double metricValue = 0;
        if (!foundPhrase.trim().equals("")) {
            metricValue = 1;
        }
                    
        // create metrics object
        MetricMessage metricMessage = MetricMessageFactory.createURLCallMessage(
                agent.urlAddress, agent.searchPhrase, metricValue);
        
        return new MetricMessageWrapper(metricMessage);
    }
    
}
