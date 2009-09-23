package org.allmon.client.aggregator;

import javax.jms.ConnectionFactory;

import org.allmon.client.agent.MetricMessageFactory;
import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class AgentAggregatorMain {

    static {
        //Properties prop = new Properties();
        //String file = prop.getProperty("log4jpropertiespath");
        String file = "log4j.properties";
        PropertyConfigurator.configure(file);
        System.out.println("log4j.configured");
    }
    
    private static final Log logger = LogFactory.getLog(AgentAggregatorMain.class);
    //private static Logger logger = Logger.getLogger(AgentAggregatorMain.class);
    
    public static void main(String args[]) throws Exception {
        logger.debug("begin");
        CamelContext context = new DefaultCamelContext();
        // Set up the ActiveMQ JMS Components
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
        
        logger.debug("end");
    }

}
