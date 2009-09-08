package org.allmon.client.aggregator;

import org.allmon.client.agent.MetricMessageFactory;
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
        Object[] tab = new Object[] { 
                MetricMessageFactory.createActionClassMessage("class1", "user1", "sess1", null), 
                MetricMessageFactory.createActionClassMessage("class2", "user1", "sess1", null), 
                MetricMessageFactory.createActionClassMessage("class3", "user1", "sess1", null)};
        
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived(tab);
        
        template.sendBodyAndHeader("direct:start", tab[0], "id", "1");
        template.sendBodyAndHeader("direct:start", tab[1], "id", "2");
        template.sendBodyAndHeader("direct:start", tab[2], "id", "2");
        
        assertMockEndpointsSatisfied();
        
        //resultEndpoint.assertExchangeReceived(1);
        System.out.println(resultEndpoint.getReceivedCounter());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .aggregate(new AgentAggregationStrategyForMetrics()).body()
                        //.aggregate().body()
                        .to(resultEndpoint); // to("mock:result");
            }
        };
    }
    
}