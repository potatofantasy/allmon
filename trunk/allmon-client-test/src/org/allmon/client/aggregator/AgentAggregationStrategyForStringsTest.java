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
        
        String expectedString = AgentAggregationStrategyString.concat("", tab[0]);
        expectedString = AgentAggregationStrategyString.concat(expectedString, tab[1]);
        expectedString = AgentAggregationStrategyString.concat(expectedString, tab[2]);
        
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived(expectedString);
        
        template.sendBodyAndHeader("direct:start", tab[0], "id", "1");
        template.sendBodyAndHeader("direct:start", tab[1], "id", "2");
        template.sendBodyAndHeader("direct:start", tab[2], "id", "2");

        assertMockEndpointsSatisfied();
        
        System.out.println(resultEndpoint.getReceivedExchanges());
        System.out.println(resultEndpoint.getReceivedCounter());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .aggregate(new AgentAggregationStrategyString())
                        .constant(null) 
                        //.body(String.class) // doesn't work for this
                        .to(resultEndpoint); // to("mock:result");
            }
        };
    }
    
}