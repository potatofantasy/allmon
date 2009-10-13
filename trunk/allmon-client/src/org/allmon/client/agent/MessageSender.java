package org.allmon.client.agent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO review association (aggregation) with MetricMessageSender - maybe it should be a supper type 
// TODO clean up the code
class MessageSender {

    private final static Log logger = LogFactory.getLog(MessageSender.class);
    
    private Destination destination;
    
    private final static long sleepTime = 0;
    private final static boolean verbose = false;
    //private final static int messageSize = 255;
    //private final static long timeToLive = 0;
    private final static int logLineLenght = 100;
    
    
    private final static boolean topic = false;
    private final static boolean transacted = false;
    private final static boolean persistent = false;
    
    private static ConnectionFactory cf; //PooledConnectionFactory pcf;
    
    static {
        System.out.println("Connecting to URL: " + AllmonCommonConstants.CLIENT_BROKER_URL);
        //System.out.println("Publishing a Message with size " + messageSize + " to " + (topic ? "topic" : "queue") + ": " + subject);
        System.out.println("Using " + (persistent ? "persistent" : "non-persistent") + " messages");
        System.out.println("Sleeping between publish " + sleepTime + " ms");
        //if (timeToLive != 0) {
        //    System.out.println("Messages time to live " + timeToLive + " ms");
        //}
        
        //pcf = new PooledConnectionFactory(new ActiveMQConnectionFactory(AllmonCommonConstants.CLIENT_BROKER_USER, AllmonCommonConstants.CLIENT_BROKER_PASSWORD, url));
        cf = AllmonActiveMQConnectionFactory.client();
        
    }
    
    public void sendMessage(MetricMessage metricMessage) {
        Connection connection = null;
        try {
            
            // Create the connection.
            //ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            //connection = connectionFactory.createConnection();
            connection = cf.createConnection();
            
            connection.start();
            
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
            ObjectMessage message = session.createObjectMessage(metricMessage);
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
            
            //System.out.println("Done.");

            // Use the ActiveMQConnection interface to dump the connection stats
            //ActiveMQConnection c = (ActiveMQConnection)connection;
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