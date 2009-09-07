package org.allmon.client.aggregator;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.CamelTestSupport;

public class AgentAggregationStrategyForStringsTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    
    public void testAggregator() throws Exception {
        String[] tab = new String[] {"class1", "class2", "class3"};
        
        resultEndpoint.expectedMessageCount(3);
        //resultEndpoint.expectedBodiesReceived(tab);
        
        template.sendBodyAndHeader("direct:start", tab[0], "id", "1");
        template.sendBodyAndHeader("direct:start", tab[1], "id", "2");
        template.sendBodyAndHeader("direct:start", tab[2], "id", "2");

        assertMockEndpointsSatisfied();
        
        System.out.println(resultEndpoint.getReceivedExchanges().toString());
        
        //resultEndpoint.assertExchangeReceived(2);
        System.out.println(resultEndpoint.getReceivedCounter());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .aggregate(new AgentAggregationStrategyString()).body()
                        //.aggregate().body()
                        .to(resultEndpoint); // to("mock:result");
            }
        };
    }
    
}