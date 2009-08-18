package org.allmon.common;

import org.apache.activemq.ActiveMQConnection;

public class AllmonCommonConstants {

    // TODO move below settings to properties files
    public final static String CLIENT_BROKER_USER = ActiveMQConnection.DEFAULT_USER;
    public final static String CLIENT_BROKER_PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    public final static String CLIENT_BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    public final static String SERVER_BROKER_USER = ActiveMQConnection.DEFAULT_USER;
    public final static String SERVER_BROKER_PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    public final static String SERVER_BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    
    public final static String CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA = "QUEUE.AGENTDATA";
    public final static String CLIENT_BROKER_QUEUE_SUBJECT_AGGREGATED = "QUEUE.AGGREGATED";
    public final static String SERVER_BROKER_QUEUE_SUBJECT_AGGREGATED = "QUEUE.READYFORLOADING";
    
    public final static String CLIENT_CAMEL_JMSQUEUE = "allmon-jms"; 
    public final static String CLIENT_CAMEL_QUEUE_AGENTSDATA = CLIENT_CAMEL_JMSQUEUE + ":queue:" + CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA;
    public final static String CLIENT_CAMEL_QUEUE_AGGREGATED = CLIENT_CAMEL_JMSQUEUE + ":queue:" + CLIENT_BROKER_QUEUE_SUBJECT_AGGREGATED; // queue with aggregates
    public final static String SERVER_CAMEL_QUEUE_LOADER = CLIENT_CAMEL_JMSQUEUE + ":queue:" + SERVER_BROKER_QUEUE_SUBJECT_AGGREGATED; // "file://" + SERVER_BROKER_QUEUE_SUBJECT_AGGREGATED;
    
}