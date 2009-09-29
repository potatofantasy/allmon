package org.allmon.client.aggregator;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;

class AgentAggregationStrategyForMetrics implements AggregationStrategy {
    
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newIn = newExchange.getIn();
        
        MetricMessageWrapper oldBodyMetricMessageWrapper = null;
        
        if (oldExchange != null) {
            Message oldIn = oldExchange.getIn();
            if (oldIn != null) {
                Object body = oldIn.getBody();
                //if (body instanceof MetricMessage) {
                //    oldBodyMetricMessage = (MetricMessage)oldIn.getBody(MetricMessage.class);
                //} else if (body instanceof MetricMessageWrapper) {
                if (body instanceof MetricMessageWrapper) {
                    oldBodyMetricMessageWrapper = (MetricMessageWrapper)oldIn.getBody(MetricMessageWrapper.class);
                } else {
                    throw new RuntimeException("Unknown metric message class found in oldExchange Message");
                }
            }
            else {
                System.out.println("]]]]]]] MyAggregationStrategy - oldIn was null");
            }
        } else {
            System.out.println("]]]]]]] MyAggregationStrategy - oldExchange was null");
        }
        
        if (newIn != null) {
            MetricMessage newBodyMetricMessage = null;
            MetricMessageWrapper newBodyMetricMessageWrapper = null;
            
            Object body = newIn.getBody();
            if (body != null) {
                if (body instanceof MetricMessage) {
                    newBodyMetricMessage = (MetricMessage)newIn.getBody(MetricMessage.class);
                } else if (body instanceof MetricMessageWrapper) {
                    newBodyMetricMessageWrapper = (MetricMessageWrapper)newIn.getBody(MetricMessageWrapper.class);
                } else {
                    throw new RuntimeException("Unknown metric message class found in newExchange Message");
                }
            }
            
            // adding a new newBodyMetricMessage to old wrapper or creating a new wrapper for old exchange 
            if (oldBodyMetricMessageWrapper != null) {
                if (newBodyMetricMessage != null) {
                    oldBodyMetricMessageWrapper.add(newBodyMetricMessage);
                } else if (newBodyMetricMessageWrapper != null) {
                    oldBodyMetricMessageWrapper.add(newBodyMetricMessageWrapper);
                } else {
                    throw new RuntimeException("Nothing to add during this aggregation the new exchanges was null");
                }
            } else {
                oldBodyMetricMessageWrapper = new MetricMessageWrapper();
                if (newBodyMetricMessage != null) {
                    oldBodyMetricMessageWrapper.add(newBodyMetricMessage);
                } else if (newBodyMetricMessageWrapper != null) {
                    oldBodyMetricMessageWrapper.add(newBodyMetricMessageWrapper);
                } else {
                    throw new RuntimeException("Nothing to add during this aggregation the new exchanges was null");
                }
            }
            newIn.setBody(oldBodyMetricMessageWrapper); // XXX check it 
        }
        else {
            //System.out.println("]]]]]]] MyAggregationStrategy - newIn was null");
            throw new RuntimeException("The newExchange Message cannot be null");
        }
        
        System.out.println("]]]]]]] MyAggregationStrategy - newBody = " + newIn.getBody().toString());
        return newExchange;
    }

}
