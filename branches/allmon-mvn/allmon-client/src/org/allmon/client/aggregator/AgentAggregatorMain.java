package org.allmon.client.aggregator;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.AllmonPropertiesValidator;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Aggregator process is responsible for:
 * (1) aggregating metrics and collecting them into packages, 
 * sending aggregated data to another queue (which might be potentially persisted), 
 * (2) sending data from aggregated metrics queue across network to loader 
 * module (server-side of allmon).
 * 
 */
public class AgentAggregatorMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AgentAggregatorMain.class);
    
    public static void main(String args[]) throws Exception {
        logger.debug(AllmonLoggerConstants.ENTERED);
        
        // validating mandatory properties, 
        // if one of mandatory properties are not declared properly terminate the program
        AllmonPropertiesValidator validator = new AllmonPropertiesValidator();
        if (!validator.validateMandatoryProperties()) {
            System.exit(1);
        }
        
        // Starting camel context and setting up the ActiveMQ JMS Components
        logger.debug("Creating camel context and routes");
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.client();
        context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(new AgentAggregatorRouteBuilder());
        context.start();
        logger.debug("Camel context has been started");
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }

}
