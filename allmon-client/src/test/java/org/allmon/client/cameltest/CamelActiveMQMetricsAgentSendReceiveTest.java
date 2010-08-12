package org.allmon.client.cameltest;

import javax.jms.ConnectionFactory;

import junit.framework.TestCase;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelActiveMQMetricsAgentSendReceiveTest extends TestCase {
    
    public void testSendAgentMessages() throws Exception {
        CamelContext context = new DefaultCamelContext();
        sendMetrics(context) ;
        //receiveMetrics(context);
        context.start();
    }
    
    private void sendMetrics(CamelContext context) throws Exception {
        // Set up the ActiveMQ JMS Components
        ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.client();
        context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE, JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        // creating messages and sending
        ProducerTemplate template = context.createProducerTemplate();
        for (int i = 0; i < 100; i++) {
            MetricMessage metricMessage = MetricMessageFactory.createClassMessage("class" + i, "method", "classNameX", "methodNameX");
            template.sendBodyAndHeader(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA, metricMessage, "MyMessage", "MyMessage");
            Thread.sleep((long)(Math.random() * 100));
        }
    }
    
    private void receiveMetrics(CamelContext context) {
        ConsumerTemplate template = context.createConsumerTemplate();
        //context.addRoutes(new AgentAggreagatorRouteBuilder());
        Object object = template.receiveBody(AllmonCommonConstants.ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA);
        System.out.println(object.toString());
    }
    
}
