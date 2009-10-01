package org.allmon.client.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class AgentAggregationStrategyString implements AggregationStrategy {
    
    private static final Log logger = LogFactory.getLog(AgentAggregationStrategyString.class);
        
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newIn = newExchange.getIn();
        
        String oldBody = "";
        if (oldExchange != null) {
            Message oldIn = oldExchange.getIn();
            if (oldIn != null) {
                oldBody = (String)oldIn.getBody(String.class);
            }
            else {
                logger.debug("]]]]]]] MyAggregationStrategy - oldIn was null");
            }
        }
        else {
            logger.debug("]]]]]]] MyAggregationStrategy - oldExchange was null");
        }
        
        String newBody = "";
        if (newIn != null) {
            newBody = (String)newIn.getBody(String.class);
            newIn.setBody(concat(oldBody, newBody));
        } 
//        else {
//            logger.debug("]]]]]]] MyAggregationStrategy - newIn was null");
//        }
        
        logger.debug("]]]]]]] MyAggregationStrategy - newBody = " + newIn.getBody(String.class));
        return newExchange;
    }
    
    static String concat(String oldBody, String newBody) {
        return oldBody + "," + newBody;
    }
    
}