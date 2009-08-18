package org.allmon.client.aggregator;

import org.allmon.common.MetricMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class AgentAggregationStrategyForMetrics implements AggregationStrategy {
    
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newIn = newExchange.getIn();
        
        MetricMessage oldBodyMetricMessage = null;
        MetricMessageWrapper oldBodyMetricMessageWrapper = null;
        if (oldExchange != null) {
            Message oldIn = oldExchange.getIn();
            if (oldIn != null) {
                Object body = oldIn.getBody();
                if (body instanceof MetricMessage) {
                    oldBodyMetricMessage = (MetricMessage)oldIn.getBody(MetricMessage.class);
                } else if (body instanceof MetricMessage) {
                    oldBodyMetricMessageWrapper = (MetricMessageWrapper)oldIn.getBody(MetricMessageWrapper.class);
                } else {
                    throw new RuntimeException("Unknown metric message class found");
                }
            }
            else {
                System.out.println("]]]]]]] MyAggregationStrategy - oldIn was null");
            }
        }
        else {
            System.out.println("]]]]]]] MyAggregationStrategy - oldExchange was null");
        }
        
        MetricMessageWrapper newBodyMetricMessageWrapper;
        if (newIn != null) {
            newBodyMetricMessageWrapper = (MetricMessageWrapper)newIn.getBody(MetricMessageWrapper.class);
            if (oldBodyMetricMessage != null) {
                newBodyMetricMessageWrapper.add(oldBodyMetricMessage);
            } else {
                newBodyMetricMessageWrapper.add(oldBodyMetricMessageWrapper);
            }
            //newIn.setBody(oldBody + "," + newBody);
            newIn.setBody(newBodyMetricMessageWrapper);
        }
//        else {
//            System.out.println("]]]]]]] MyAggregationStrategy - newIn was null");
//        }
        
//        System.out.println("]]]]]]] MyAggregationStrategy - newBody = " + newIn.getBody(String.class));
        return newExchange;
    }

}
