package org.allmon.client.agent;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.AggregationCollection;
import org.apache.camel.processor.aggregate.DefaultAggregationCollection;
import org.apache.camel.test.CamelTestSupport;

public class SimpleCamelAggregatorTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    public void testAggregator() throws Exception {
        resultEndpoint.expectedMessageCount(5);
        resultEndpoint.expectedBodiesReceived(new Object[] { "100", "150", "130", "200", "190" });

        // then we sent all the message at once
        template.sendBodyAndHeader("direct:start", "100", "id", "1");
        template.sendBodyAndHeader("direct:start", "150", "id", "2");
        template.sendBodyAndHeader("direct:start", "130", "id", "2");
        template.sendBodyAndHeader("direct:start", "200", "id", "1");
        template.sendBodyAndHeader("direct:start", "190", "id", "1");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // AggregationCollection ag = new
                // PredicateAggregationCollection(header("id"),
                // new UseLatestAggregationStrategy(),
                // property(Exchange.AGGREGATED_SIZE).isEqualTo(3));

                AggregationCollection ag = new DefaultAggregationCollection();

                // our route is aggregating from the direct queue and sending
                // the response to the mock
                from("direct:start") // from("direct:start")
                        // we use the collection based aggregator which we
                        // already have configured
                        // .aggregate(ag)
                        .aggregate().body()
                        //.aggregate().header("1")
                        .to(resultEndpoint); // to("mock:result");
            }
        };
    }

}
