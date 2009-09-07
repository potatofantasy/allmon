package org.allmon.client.aggregator;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.processor.aggregate.AggregationCollection;
import org.apache.camel.processor.aggregate.AggregationStrategy;

//public class AgentAggregationCollection extends AbstractCollection implements AggregationCollection {
class AgentAggregationCollection extends AbstractCollection<Exchange> implements AggregationCollection {

    private List collection = new ArrayList();
    private Expression correlation;
    private AggregationStrategy strategy; // = new UseLatestAggregationStrategy();

    public AgentAggregationCollection() {
        super();
        System.out.println("AgentAggregationCollection>>>>");
    }
    
    public Expression getCorrelationExpression() {
        return correlation;
    }

    public void setCorrelationExpression(Expression correlationExpression) {
        this.correlation = correlationExpression;
    }

    public AggregationStrategy getAggregationStrategy() {
        return strategy;
    }

    public void setAggregationStrategy(AggregationStrategy aggregationStrategy) {
        this.strategy = aggregationStrategy;
        System.out.println("setAggregationStrategy>>>>");
    }

    public boolean add(Exchange exchange) {
        System.out.println("add>>>>");
        return collection.add(exchange);
    }

    public Iterator iterator() {
        // demonstrate the we can do something with this collection, so we reverse it
        // Collections.reverse(collection);
        return collection.iterator();
    }

    public int size() {
        return collection.size();
    }

    public void clear() {
        collection.clear();
    }

    public void onAggregation(Object correlationKey, Exchange newExchange) {
        add(newExchange);
        System.out.println("added>>>>");
    }
}
