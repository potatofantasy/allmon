package org.allmon.client.aggregator;

import org.allmon.client.agent.MetricMessageFactory;
import org.allmon.common.MetricMessage;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.CamelTestSupport;

public class AgentAggregationStrategyForMetricsTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    
    public void testAggregator() throws Exception {
        MetricMessage[] tab = new MetricMessage[] { 
                MetricMessageFactory.createActionClassMessage("class1", "user1", "sess1", null), 
                MetricMessageFactory.createActionClassMessage("class2", "user2", "sess1", null), 
                MetricMessageFactory.createActionClassMessage("class3", "user3", "sess1", null)};
        
        MetricMessageWrapper expectedMessageWrapper = new MetricMessageWrapper();
        expectedMessageWrapper.add(tab[0]);
        expectedMessageWrapper.add(tab[1]);
        expectedMessageWrapper.add(tab[2]);
        
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived(expectedMessageWrapper);
        
        template.sendBodyAndHeader("direct:start", tab[0], "id", "1");
        template.sendBodyAndHeader("direct:start", tab[1], "id", "2");
        template.sendBodyAndHeader("direct:start", tab[2], "id", "3");
        
        assertMockEndpointsSatisfied();

        System.out.println(resultEndpoint.getReceivedExchanges());
        System.out.println(resultEndpoint.getReceivedCounter());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .aggregate(new AgentAggregationStrategyForMetrics())
                        .constant(null) // XXX check it , still doesn't work even if expected object is the same 
                        //.body(MetricMessageWrapper.class) // doesn't work for this
                        .to(resultEndpoint); // to("mock:result");
            }
        };
    }
    
}
