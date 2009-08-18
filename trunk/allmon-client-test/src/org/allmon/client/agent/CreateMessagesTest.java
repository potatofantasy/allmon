package org.allmon.client.agent;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class CreateMessagesTest {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();
        // Set up the ActiveMQ JMS Components
        //ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.client();
        
        context.addComponent(AllmonCommonConstants.CLIENT_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        // producing 
        ProducerTemplate template = context.createProducerTemplate();
        context.start();
        for (int i = 0; i < 1000; i++) {
            template.sendBodyAndHeader(AllmonCommonConstants.CLIENT_CAMEL_QUEUE_AGENTSDATA, "M:" + i, "MyMessage", "MyMessage");
            //Thread.sleep((long)(Math.random() * 100));
        }
        
        context.stop();
        System.exit(0);
    }
    
}
