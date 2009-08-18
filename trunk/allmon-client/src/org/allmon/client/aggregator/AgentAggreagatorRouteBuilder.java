package org.allmon.client.aggregator;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class AgentAggreagatorRouteBuilder extends RouteBuilder {

    // TODO parametrise this !!!
    private final static int aggregatorBatchSize = 10; 
    private final static long aggregatorBatchTimeout = 1000L;
    
    public void configure() {
        // for camel-2.0
//        AggregateDefinition aggDef = from(q1).aggregate(new AgentAggregationStrategyString()).constant("");
//        aggDef.batchSize(aggregatorBatchSize).batchTimeout(aggregatorBatchTimeout).to(q2);

//        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA).aggregate(new AgentAggregationCollection()).
//            batchSize(aggregatorBatchSize).batchTimeout(aggregatorBatchTimeout).to(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED);
        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA).aggregate().body(MetricMessage.class).
            batchSize(aggregatorBatchSize).batchTimeout(aggregatorBatchTimeout).to(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED);
        
        // for camel-1.6.x
        // aggregating data from agents queue and passing them to aggregated queue
//        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA).aggregator(new AgentAggregationStrategyString()). //constant(""). //header(""). //constant("").
//            batchSize(aggregatorBatchSize).batchTimeout(aggregatorBatchTimeout).to(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED);
        
//        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA).aggregator(new AgentAggregationCollection()).
//            batchSize(aggregatorBatchSize).batchTimeout(aggregatorBatchTimeout).to(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED);
        
        // 
//        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED).to(AllmonCommonConstants.SERVER_CAMEL_QUEUE_LOADER);
        
        // for debugging purposes
        from(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGGREGATED).process(new Processor() {
            public void process(Exchange e) {
                System.out.println(">>>>> Received exchange: " + e.getIn());
                System.out.println(">>>>> Received exchange body: " + e.getIn().getBody());
                //System.out.println(">>>>> Received exchange: " + e.getOut());
            }
        });
        
        /*
        from("file:src/data?noop=true").convertBodyTo(PersonDocument.class)
            .to("jpa:org.apache.camel.example.etl.CustomerEntity");
        // the following will dump the database to files
        from("jpa:org.apache.camel.example.etl.CustomerEntity?consumeDelete=false&consumer.delay=3000&consumeLockEntity=false")
            .setHeader(Exchange.FILE_NAME, el("${in.body.userName}.xml"))
            .to("file:target/customers?append=false");
        */
        
    }
    
}