package org.allmon.client.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;

class AgentAggregationStrategyString implements AggregationStrategy {
    
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newIn = newExchange.getIn();
        
        String oldBody = "";
        if (oldExchange != null) {
            Message oldIn = oldExchange.getIn();
            if (oldIn != null) {
                oldBody = (String)oldIn.getBody(String.class);
            }
            else {
                System.out.println("]]]]]]] MyAggregationStrategy - oldIn was null");
            }
        }
        else {
            System.out.println("]]]]]]] MyAggregationStrategy - oldExchange was null");
        }
        
        String newBody = "";
        if (newIn != null) {
            newBody = (String)newIn.getBody(String.class);
            newIn.setBody(oldBody + "," + newBody);
        } 
//        else {
//            System.out.println("]]]]]]] MyAggregationStrategy - newIn was null");
//        }
        
        System.out.println("]]]]]]] MyAggregationStrategy - newBody = " + newIn.getBody(String.class));
        return newExchange;
    }
    
    static String concat(String oldBody, String newBody) {
        return oldBody + "," + newBody;
    }
    
}