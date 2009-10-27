package org.allmon.client.agent;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.util.IndentPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
class MessageSender {

    private final static Log logger = LogFactory.getLog(MessageSender.class);
    
    private Destination destination;
    
    private final static long sleepTime = 0;
    private final static boolean verbose = false;
    //private final static int messageSize = 255;
    private final static long timeToLive = 0;
    private final static int logLineLenght = 100;
    
    private final static boolean topic = false;
    private final static boolean transacted = false;
    private final static boolean persistent = false;
    
    private static ConnectionFactory cf; //PooledConnectionFactory pcf;
    
    static {
        logger.debug("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
        //logger.debug("Publishing a Message with size " + messageSize + " to " + (topic ? "topic" : "queue") + ": " + subject);
        logger.debug("Using " + (persistent ? "persistent" : "non-persistent") + " messages");
        logger.debug("Sleeping between publish " + sleepTime + " ms");
        if (timeToLive != 0) {
            logger.debug("Messages time to live " + timeToLive + " ms");
        }

        //JmsBrokerHealthSampler.getInstance().checkJmsBrokerIsUp();
        
        //cf = new PooledConnectionFactory(new ActiveMQConnectionFactory(AllmonCommonConstants.CLIENT_BROKER_USER, AllmonCommonConstants.CLIENT_BROKER_PASSWORD, url));
        cf = AllmonActiveMQConnectionFactory.client(); // is using pooled connections
    }
    
    public void sendMessage(Serializable messageObject) {
        Connection connection = null;
        try {
            // Create the connection.
            //ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            //connection = connectionFactory.createConnection();
            connection = cf.createConnection();
            
            connection.start(); // XXX application hangs here if a jms broker is down!!!
            
            // Create the session
            Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            if (topic) {
                destination = session.createTopic(AllmonCommonConstants.CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA);
            } else {
                destination = session.createQueue(AllmonCommonConstants.CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA);
            }
            
            // Create the producer.
            MessageProducer producer = session.createProducer(destination);
            if (persistent) {
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
            //if (timeToLive != 0) {
            //    producer.setTimeToLive(timeToLive);
            //}

            // Start sending messages
            //TextMessage message = session.createTextMessage(text);
            ObjectMessage message = session.createObjectMessage(messageObject);
            if (verbose) {
                //String msg = message.getText();
                String msg = message.toString();
                if (msg.length() > logLineLenght) {
                    msg = msg.substring(0, logLineLenght) + "...";
                }
                //System.out.println("Sending message: " + msg);
            }
            producer.send(message);
            if (transacted) {
                session.commit();
            }
            
            //logger.debug("Data has been sent successfully");

            // Use the ActiveMQConnection interface to dump the connection stats
            //ActiveMQConnection c = (ActiveMQConnection)cf;
            //c.getConnectionStats().dump(new IndentPrinter());

        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable t) {
                logger.error(t);
            }
        }
    }
    
}