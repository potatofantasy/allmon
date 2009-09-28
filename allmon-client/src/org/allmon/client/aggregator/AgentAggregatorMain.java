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
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.client();
        context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(new AgentAggregatorRouteBuilder());
        context.start();
        logger.debug("camel context has been started");
        
        // debug - creating messages
//        Thread.sleep(1000);
//        ProducerTemplate template = context.createProducerTemplate();
//        for (int i = 0; i < 100; i++) {
//            // debug for strings messages
////            String stringMessage = "M:" + i + " ";
////            template.sendBodyAndHeader(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA, stringMessage, "MyMessage", "MyMessage");
//            // debug for metrics messages 
//            MetricMessage metricMessage = MetricMessageFactory.createClassMessage("class" + i, "method", "user", (long)(Math.random() * 1000));
//            template.sendBodyAndHeader(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA, metricMessage, "MyMessage", "MyMessage");
//            Thread.sleep((long)(Math.random() * 100));
//        }
        //Thread.sleep(100 * 365 * 60 * 60 * 1000); // 100 years
        //context.stop();
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }

}
