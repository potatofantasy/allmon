package org.allmon.client.aggregator;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.MetricMessageWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for defining two camel routes:
 * (1) for aggregating metrics and collecting them into packages, 
 * sending aggregated data to another queue (which might be potentially persisted), 
 * (2) for sending data from aggregated metrics queue across network to loader 
 * module (server-side of allmon).
 * 
 */
class AgentAggregatorRouteBuilder extends RouteBuilder {

    private static Log logger = LogFactory.getLog(AgentAggregatorRouteBuilder.class);
    
    private boolean verboseLogging = AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_VERBOSELOGGING;
    
    public void configure() {
        logger.debug(AllmonLoggerConstants.ENTERED);
        
        // collecting metrics data from agents, aggregating them and sending to the aggregated messages queue
        // for camel-2.0
        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA).
            aggregate(new AgentAggregationStrategyForMetrics()).body(MetricMessageWrapper.class). //constant(null).
            batchSize(AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_BATCHSIZE).
            batchTimeout(AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT).
            to(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED);
        
        // taking messages from aggregated metrics messages queue and passing them to server (allmon loader queue)
        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED).to(AllmonCommonConstants.ALLMON_SERVER_CAMEL_QUEUE_READYFORLOADING);
        
//        // XXX kept for debugging purposes only
//        if (verboseLogging){
//	        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED).process(new Processor() {
//	            public void process(Exchange e) {
//	                logger.debug(">>>>> Received exchange: " + e.getIn());
//	                logger.debug(">>>>> Received exchange body: " + ((e.getIn() != null)?e.getIn().getBody():" e.getIn is null"));
//	                //logger.debug(">>>>> Received exchange: " + e.getOut());
//	                //logger.debug(">>>>> Received exchange body: " + ((e.getOut() != null)?e.getOut().getBody():"null"));
//	            }
//	        });
//        }
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
