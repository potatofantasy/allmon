package org.allmon.client.agent;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.allmon.common.AllmonCommonConstants;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * This class is responsible for sending serializable data 
 * to client-side JMS broker.
 * 
 */
class MessageSender {

    private final static Log logger = LogFactory.getLog(MessageSender.class);
    
//    private Destination destination;
    
    private final static long sleepTime = 0;
//    private final static boolean verbose = false;
    //private final static int messageSize = 255;
    private final static long timeToLive = 0;
//    private final static int logLineLenght = 100;
    
//    private final static boolean topic = false;
//    private final static boolean transacted = false;
    private final static boolean persistent = false;
    
//    private final ConnectionFactory cf; //PooledConnectionFactory pcf;
    
//    MessageSender(ConnectionFactory cf) {
//        this.cf = cf;
    MessageSender() {
    	
        logger.debug("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
        //logger.debug("Publishing a Message with size " + messageSize + " to " + (topic ? "topic" : "queue") + ": " + subject);
        logger.debug("Using " + (persistent ? "persistent" : "non-persistent") + " messages");
        logger.debug("Sleeping between publish " + sleepTime + " ms");
        if (timeToLive != 0) {
            logger.debug("Messages time to live " + timeToLive + " ms");
        }
        
        //JmsBrokerHealthSampler.getInstance().checkJmsBrokerIsUp();
        
        //cf = new PooledConnectionFactory(new ActiveMQConnectionFactory(AllmonCommonConstants.CLIENT_BROKER_USER, AllmonCommonConstants.CLIENT_BROKER_PASSWORD, url));
        //cf = AllmonActiveMQConnectionFactory.client(); // is using pooled connections
        
    	//logPoolStats();
    	
    }
    
    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
    		new String[] { "classpath:META-INF/allmonAgentAppContext-jms.xml" });
    
    private JmsTemplate jmsTemplate = (JmsTemplate)context.getBean("allmonSenderJmsTemplate");
    
    private Serializable messageObject;
    
	public Serializable getMessageObject() {
		return messageObject;
	}
    
    public void sendMessage(Serializable messageObject) {
    	this.messageObject = messageObject;
    	jmsTemplate.send(
    			AllmonCommonConstants.CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA, 
    			new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(getMessageObject());
					}
				});
//    	logPoolStats();
    }
    
    void logPoolStats() {
    	PooledConnectionFactory pcf = (PooledConnectionFactory)context.getBean("jmsFactory"); //(PooledConnectionFactory)cf;
    	logger.debug(">>> IdleTimeout: " + pcf.getIdleTimeout());
    	logger.debug(">>> MaxConnections:" + pcf.getMaxConnections());
    	logger.debug(">>> MaximumActive:" + pcf.getMaximumActive());
    }
    
}