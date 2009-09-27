package org.allmon.client.agent;

import org.allmon.client.aggregator.AgentAggregatorRouteBuilder;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.CamelTestSupport;

public class AgentAggregatorRouteBuilderTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    //@EndpointInject(uri = AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE)
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    
    public void testAggregator() throws Exception {
        Object[] tab = new Object[] {
                MetricMessageFactory.createActionClassMessage("class1", "user1", "sess1", null), 
                MetricMessageFactory.createActionClassMessage("class2", "user1", "sess1", null), 
                MetricMessageFactory.createActionClassMessage("class3", "user1", "sess1", null) };
        
        resultEndpoint.expectedMessageCount(3);
        resultEndpoint.expectedBodiesReceived(tab);
        
        template.sendBodyAndHeader("direct:start", tab[0], "id", "1");
        template.sendBodyAndHeader("direct:start", tab[1], "id", "2");
        template.sendBodyAndHeader("direct:start", tab[2], "id", "3");
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new AgentAggregatorRouteBuilder();
    }
    
    protected RouteBuilder createRouteBuilderDummy() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .aggregate().body()
                        //.aggregate().body(MetricMessage.class)
                        //.aggregate(new AgentAggregationCollection()) // XXX it doesn't work for the custom collection
                        //.aggregate(new DefaultAggregationCollection()) // XXX it doesn't work for it as well
                        .batchSize(10)
                        .to(resultEndpoint); // to("mock:result");
            }
        };
    }

}
