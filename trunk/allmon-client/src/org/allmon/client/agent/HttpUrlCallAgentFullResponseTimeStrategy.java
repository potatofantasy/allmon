package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Acquired metrics values represent full time between connection 
 * and deciding about if token can be found in the page content
 * (response time + transferring all content + parsing [searching token] 
 * in the content).
 * 
 */
public class HttpUrlCallAgentFullResponseTimeStrategy extends HttpUrlCallAgentAbstractStrategy {

    private static final Log logger = LogFactory.getLog(HttpUrlCallAgentFullResponseTimeStrategy.class);
    
    MetricMessageWrapper extractMetrics() {
        String foundPhrase = OutputParser.findFirst(bufferedReaderCallResponse, agent.searchPhrase);

        double metricValue = 0;
        if (!foundPhrase.trim().equals("")) {
            // response time + transferring all content + parsing
            metricValue = System.currentTimeMillis() - agent.stratTime;
        }

        // create metrics object
        MetricMessage metricMessage = MetricMessageFactory.createUrlCallMessage(
                agent.checkName, agent.instanceName, agent.checkingHost, metricValue, null);

        return new MetricMessageWrapper(metricMessage);
    }

}
