package org.allmon.client.controller.neuralrules;

import org.allmon.client.controller.terminator.NeuralRulesJavaCallTerminatorController;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NeuralRulesReceiverRouteBuilder extends RouteBuilder {

    private static final Log logger = LogFactory.getLog(NeuralRulesReceiverRouteBuilder.class);
    
	private boolean verboseLogging = AllmonCommonConstants.ALLMON_SERVER_RECEIVER_VERBOSELOGGING;
    
	public final static String ALLMON_BROKER_QUEUE_NEURALRULES_FORCONTROLLER = 
		"QUEUE.CONTROLLER.NEURALRULES";
	
	public void configure() {
		logger.debug(AllmonLoggerConstants.ENTERED);
        
        from(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE + ":queue:" +
        		ALLMON_BROKER_QUEUE_NEURALRULES_FORCONTROLLER).process(new Processor() {
            public void process(Exchange e) {
            	if (verboseLogging) {
            		//System.out.println(">>>>> Received exchange: " + e.getIn());
                    logger.debug(">>>>> Received exchange: " + e.getIn());
	                logger.debug(">>>>> Received exchange body: " + e.getIn().getBody());
            	}
            	
            	String xml = (String)e.getIn().getBody();
            	NeuralRulesNeuroph neuralRules = NeuralRulesNeuroph.instantiateSerialized(xml);
                if (neuralRules != null) {
                    try {
                		//logger.debug(">>>>> >>>>> metric message: " + neuralRules);
                		//System.out.println(">>>>> >>>>> metric message: " + neuralRules);
                		NeuralRulesJavaCallTerminatorController.neuralRulesMap.put(
                				neuralRules.getAction(), neuralRules);
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }
                } else {
                    logger.debug(">>>>> Received exchange: NeuralRulesNeuroph is null");
                }
                
            	if (verboseLogging) {
    	            logger.debug(">>>>> Received exchange: End.");
            	}
            }
        });
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
	
}