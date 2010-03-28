package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;

/**
 * Acquired metrics values represent full time between connection 
 * and deciding about if token can be found in the page content
 * (response time + transferring all content + parsing [searching token] 
 * in the content).
 * 
 */
public class HttpUrlCallAgentFullResponseTimeStrategy extends HttpUrlCallAgentAbstractStrategy {

//    private static final Log logger = LogFactory.getLog(HttpUrlCallAgentFullResponseTimeStrategy.class);
    
    MetricMessageWrapper extractMetrics() {
        String foundPhrase = OutputParser.findFirst(bufferedReaderCallResponse, agent.searchPhrase);

        // response time + transferring all content + parsing
        long fullResponseTime = System.currentTimeMillis() - agent.stratTime;
        
        double metricValue = 0;
        if (!foundPhrase.trim().equals("")) {
            // if phrase can be found metric value is away greater than 0
            metricValue = fullResponseTime > 0 ? fullResponseTime : 1; 
        }

        // create metrics object
        MetricMessage metricMessage = MetricMessageFactory.createUrlCallMessage(
                agent.checkName, agent.instanceName, agent.checkingHost, metricValue, null);

        return new MetricMessageWrapper(metricMessage);
    }

}
