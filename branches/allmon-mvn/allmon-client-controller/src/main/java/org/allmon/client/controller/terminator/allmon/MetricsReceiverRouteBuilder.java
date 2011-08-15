package org.allmon.client.controller.terminator.allmon;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class MetricsReceiverRouteBuilder extends RouteBuilder {

    private static final Log logger = LogFactory.getLog(MetricsReceiverRouteBuilder.class);
        
    private boolean verboseLogging = AllmonCommonConstants.ALLMON_SERVER_RECEIVER_VERBOSELOGGING;
    
    public void configure() {
    	logger.debug(AllmonLoggerConstants.ENTERED);
        
    	// receiving data from server-side queue
    	// TODO XXX evaluate running loading (storeMetric) process in a separate thread(s) - .threads(int no).
    	// TODO XXX many concurrent threads with this route should fasten loading process (especially for many independent metrics messages)
        from(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE + ":topic:" + "TOPIC.AGGREGATED.FORCONTROLLER1"
        		).process(new Processor() {
            public void process(Exchange e) {
            	if (verboseLogging) {
	                logger.debug(">>>>> Received exchange: " + e.getIn());
	                logger.debug(">>>>> Received exchange body: " + e.getIn().getBody());
            	}
            	System.out.println(">>>>> Received exchange: " + e.getIn());
                
            	
                MetricMessageWrapper metricMessageWrapper = (MetricMessageWrapper)e.getIn().getBody();
                if (metricMessageWrapper != null) {
                    try {
                        // TODO Store metrics
                        // ...
                    	for (MetricMessage metricMessage : metricMessageWrapper) {
                    		String metricMessageString = metricMessage.toString();
                    		//logger.debug(">>>>> >>>>> metric message: " + metricMessageString);
                    		System.out.println(">>>>> >>>>> metric message: " + metricMessageString);
                    		AllmonMetricsReceiver.metricsDataStore.put(metricMessageString, metricMessageString);
						}
                    	// ...
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                } else {
                    logger.debug(">>>>> Received exchange: MetricMessageWrapper is null");
                }
                
            	if (verboseLogging) {
    	            logger.debug(">>>>> Received exchange: End.");
            	}
            }
        });
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
	
}
