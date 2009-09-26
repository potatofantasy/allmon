package org.allmon.client.aggregator;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentAggregatorRouteBuilder extends RouteBuilder {

    private static Log logger = LogFactory.getLog(AgentAggregatorRouteBuilder.class);
    
    public void configure() {
        logger.debug(AllmonLoggerConstants.ENTERED);
        
        // collecting metrics data from agents, aggregating them and sending to the aggregated messages queue
        // for camel-2.0
        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA).
            aggregate(new AgentAggregationStrategyForMetrics()).body(MetricMessageWrapper.class). //constant(null).
            batchSize(AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_BATCHSIZE).
            batchTimeout(AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT).
            to(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED);
        
        // debug for String messages
//        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA).
//            aggregate(new AgentAggregationStrategyString()).constant(null). //body(String.class). //constant(null).
//            batchSize(AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_BATCHSIZE).
//            batchTimeout(AllmonCommonConstants.ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT).
//            to(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED);

        // for camel-1.6.x
        // aggregating data from agents queue and passing them to aggregated queue
//        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA).aggregator(new AgentAggregationCollection()).
//            batchSize(aggregatorBatchSize).batchTimeout(aggregatorBatchTimeout).to(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED);
        
        // taking messages from aggregated metrics messages queue and passing them to server (allmon loader queue)
        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED).to(AllmonCommonConstants.ALLMON_SERVER_CAMEL_QUEUE_READYFORLOADING);
        
        // XXX kept for debugging purposes
//        from(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED).process(new Processor() {
//            public void process(Exchange e) {
//                System.out.println(">>>>> Received exchange: " + e.getIn());
//                System.out.println(">>>>> Received exchange body: " + ((e.getIn() != null)?e.getIn().getBody():"e.getIn is null"));
//                //System.out.println(">>>>> Received exchange: " + e.getOut());
//                //System.out.println(">>>>> Received exchange body: " + ((e.getOut() != null)?e.getOut().getBody():"null"));
//            }
//        });
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
