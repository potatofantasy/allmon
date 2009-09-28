package org.allmon.server.receiver;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AggregatesReceiverMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AggregatesReceiverMain.class);
    
    public static void main(String args[]) throws Exception {
        logger.debug("begin");
        CamelContext context = new DefaultCamelContext();
        // Set up the ActiveMQ JMS Components
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.server();
        context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(new LoaderReceiverRouteBuilder());
        context.start();
        // ...
        //Thread.sleep(100 * 365 * 24 * 60 * 60 * 1000);
        //context.stop();
        logger.debug("end");
    }
    
}
