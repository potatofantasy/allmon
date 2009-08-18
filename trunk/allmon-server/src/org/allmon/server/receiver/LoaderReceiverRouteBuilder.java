package org.allmon.server.receiver;

import org.allmon.common.AllmonCommonConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class LoaderReceiverRouteBuilder extends RouteBuilder {

    public void configure() {

        // receiving data from server-side queue
        from(AllmonCommonConstants.SERVER_CAMEL_QUEUE_LOADER).process(new Processor() {
            public void process(Exchange e) {
                System.out.println(">>>>> Received exchange: " + e.getIn());
                System.out.println(">>>>> Received exchange body: " + e.getIn().getBody());
                // System.out.println(">>>>> Received exchange: " + e.getOut());
                
                // Store metric
                LoadRawMetric loadRawMetric = new LoadRawMetric();
                loadRawMetric.storeMetric(e.getIn().getBody().toString());
                System.out.println(">>>>> Received exchange: End.");
            }
        });
        
        
        //from(q2).to("jpa:org.apache.camel.example.jmstofile.PersMessage");

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
