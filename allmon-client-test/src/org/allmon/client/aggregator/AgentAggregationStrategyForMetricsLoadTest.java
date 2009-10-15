package org.allmon.client.aggregator;

import java.util.HashMap;

import junit.framework.TestCase;

import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.allmon.server.loader.LoadTestedClass;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.JmsExchange;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentAggregationStrategyForMetricsLoadTest extends TestCase {

    private static final Log logger = LogFactory.getLog(AgentAggregationStrategyForMetricsLoadTest.class);
    
    private final static long THREADS_COUNT = 5;
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 2000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 1;
    
    public void testMain() throws InterruptedException {
        logger.debug("m2 - start");
        
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        logger.debug("m2 - creating definitions of threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // creating a thread
            Thread t = new Thread(new LoadTestedClass(i, STARTING_TIME_MILLIS) {
                public void runConcurently() {
                    logger.debug("MetricMessage started");
                    long t0 = System.nanoTime();
                    
                    Endpoint endpoint = new JmsEndpoint();
                    JmsBinding jmsBinding = new JmsBinding();
                                        
                    Exchange oldExchange = null;
                    
                    AgentAggregationStrategyForMetrics aggregationStrategy = new AgentAggregationStrategyForMetrics();

                    long t1 = System.nanoTime();
                    
                    for (int i = 0; i < SUBSEQUENT_CALLS_IN_THREAD; i++) {
                        
                        // for every aggregation brand new message object is needed
                        Exchange newExchange = new JmsExchange(endpoint, ExchangePattern.InOut, jmsBinding);
                        Message message = new JmsMessage();
                        message.setBody(MetricMessageFactory.createClassMessage("className", "methodName", "user", i));
                        newExchange.setIn(message);
                        
                        // aggregating messages
                        oldExchange = aggregationStrategy.aggregate(oldExchange, newExchange);
                        
                    }
                    long t2 = System.nanoTime();
                    
                    assertEquals(SUBSEQUENT_CALLS_IN_THREAD, ((MetricMessageWrapper)oldExchange.getIn().getBody()).size());
                    
                    logger.debug("MetricMessage initialized in " + (t1 - t0)/1000000);
                    logger.debug("MetricMessage metrics sent in " + (t2 - t1)/1000000);
                                
                    logger.debug("MetricMessage end");
                }
            });
            
            loadThreadsMap.put(new Integer(i), t);
        }
        
        logger.debug("m2 - running threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            // taking a thread definition to run it
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.start();
        }
        
        logger.debug("m2 - waiting for running threads");
        for (int i = 0; i < THREADS_COUNT; i++) {
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.join();
        }
        
        logger.debug("m2 - end");
    }
    
}
