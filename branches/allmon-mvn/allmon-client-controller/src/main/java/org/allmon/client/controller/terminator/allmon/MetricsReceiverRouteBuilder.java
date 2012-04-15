package org.allmon.client.controller.terminator.allmon;

import java.util.Timer;
import java.util.TimerTask;

import org.allmon.client.agent.OsAgent;
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
        
    	// internal OSmetrics acquisition (faster and not )
    	Timer timer = new Timer("MetricsReceiverRouteBuilder-InternalOSMetricsAcqThread", true);
		TimerTask task = new TimerTask() {
			public void run() {
				MetricMessageWrapper metricMessageWrapper = OsAgent.collectMetrics("ALL");
	            if (metricMessageWrapper != null) {
	                // Store metrics
	            	for (MetricMessage metricMessage : metricMessageWrapper) {
	            		//String metricMessageString = metricMessage.toString();
	            		//logger.debug(">>>>> >>>>> metric message: " + metricMessageString);
	            		//System.out.println(">>>>> >>>>> metric message: " + metricMessageString);
	            		//System.out.println(">>>>> >>>>> metric message package received");
	            		AllmonMetricsReceiver.metricsDataStore.put(metricMessage);
	            	}
	            }
			}
		};
		timer.schedule(task, 2000, 2000);
		//timer.cancel();
    	
    	
    	// receiving data from server-side queue
    	// TODO XXX evaluate running loading (storeMetric) process in a separate thread(s) - .threads(int no).
    	// TODO XXX many concurrent threads with this route should fasten loading process (especially for many independent metrics messages)
        from(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE + ":topic:" + "TOPIC.AGGREGATED.FORCONTROLLER1"
        		).process(new Processor() {
            public void process(Exchange e) {
            	long t0 = System.currentTimeMillis();
            	if (verboseLogging) {
            		//System.out.println(">>>>> Received exchange: " + e.getIn());
                    logger.debug(">>>>> Received exchange: " + e.getIn());
	                logger.debug(">>>>> Received exchange body: " + e.getIn().getBody());
            	}
            	
                MetricMessageWrapper metricMessageWrapper = (MetricMessageWrapper)e.getIn().getBody();
                if (metricMessageWrapper != null) {
                    try {
                        // Store metrics
                    	for (MetricMessage metricMessage : metricMessageWrapper) {
                    		//String metricMessageString = metricMessage.toString();
                    		//logger.debug(">>>>> >>>>> metric message: " + metricMessageString);
                    		//System.out.println(">>>>> >>>>> metric message: " + metricMessageString);
                    		//System.out.println(">>>>> >>>>> metric message package received");
                    		AllmonMetricsReceiver.metricsDataStore.put(metricMessage);
                    	}
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                } else {
                    logger.debug(">>>>> Received exchange: MetricMessageWrapper is null");
                }
                
            	if (verboseLogging) {
    	            logger.debug(">>>>> Received exchange: End.");
            	}
            	System.out.println(">>>>> Received exchange and set data to metricsDataStore >> " + (System.currentTimeMillis() - t0) + "ms");
            }
        });
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
	
}
