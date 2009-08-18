package org.allmon.server.receiver;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class AggregatesReceiverMain {

    public static void main(String args[]) throws Exception {
        System.out.println("AggregatesReceiverMain>>>>");
        CamelContext context = new DefaultCamelContext();
        // Set up the ActiveMQ JMS Components
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.server();
        context.addComponent(AllmonCommonConstants.CLIENT_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(new LoaderReceiverRouteBuilder());
        context.start();
        // ...
        //Thread.sleep(100 * 365 * 24 * 60 * 60 * 1000);
        //context.stop();
    }
    
}
