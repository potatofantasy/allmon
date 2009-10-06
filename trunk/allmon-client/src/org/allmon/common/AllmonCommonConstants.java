package org.allmon.common;

import org.apache.activemq.ActiveMQConnection;

public class AllmonCommonConstants {

    public final static long TIMER_100YEARS_IN_MS = 100 * 365 * 24 * 3600 * 1000;
    
    // settings for connection to activemq jms broker
    public final static String CLIENT_BROKER_USER = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_USER, ActiveMQConnection.DEFAULT_USER);
    public final static String CLIENT_BROKER_PASSWORD = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
    public final static String CLIENT_BROKER_URL = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
    public final static String SERVER_BROKER_USER = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_SERVER_BROKER_USER, ActiveMQConnection.DEFAULT_USER);
    public final static String SERVER_BROKER_PASSWORD = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_SERVER_BROKER_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
    public final static String SERVER_BROKER_URL = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_SERVER_BROKER_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
    
    // allmon queues names
    public final static String CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA = "QUEUE.AGENTDATA";
    public final static String CLIENT_BROKER_QUEUE_SUBJECT_AGGREGATED = "QUEUE.AGGREGATED";
    public final static String SERVER_BROKER_QUEUE_SUBJECT_READYFORLOADING = "QUEUE.READYFORLOADING";
    
    // URIs used by camel integration patterns implementations
    public final static String ALLMON_CAMEL_JMSQUEUE = "allmon-jms"; 
    public final static String ALLMON_CLIENT_CAMEL_QUEUE_AGENTSDATA = ALLMON_CAMEL_JMSQUEUE + ":queue:" + CLIENT_BROKER_QUEUE_SUBJECT_AGENTSDATA;
    public final static String ALLMON_CLIENT_CAMEL_QUEUE_AGGREGATED = ALLMON_CAMEL_JMSQUEUE + ":queue:" + CLIENT_BROKER_QUEUE_SUBJECT_AGGREGATED; // queue with aggregates
    public final static String ALLMON_SERVER_CAMEL_QUEUE_READYFORLOADING = ALLMON_CAMEL_JMSQUEUE + ":queue:" + SERVER_BROKER_QUEUE_SUBJECT_READYFORLOADING;
    
    // for aggregator
    public final static int ALLMON_CLIENT_AGGREGATOR_BATCHSIZE = AllmonPropertiesReader.getInstance().getValueInt(AllmonPropertiesConstants.ALLMON_CLIENT_AGGREGATOR_BATCHSIZE, 10);
    public final static long ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT = AllmonPropertiesReader.getInstance().getValueInt(AllmonPropertiesConstants.ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT, 10 * 1000);
    
    
    // for allmon-server loader - TODO review moving those constants to separate class visible only for allmon-server 
    // all values must be in sync with values defined in allmon database in static dimensions
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_OS = "OS"; //"operating.system";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION = "APP"; //"application"; 
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_REPORT = "REP"; //"report";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_JVM = "JVM"; //"java";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_DB = "DB"; //"database";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_HARDWARE = "HW"; //"hardware";
    
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_ACTIONSERVLET = "ACTCLS"; //"application.action.servlet"; 
    
    
}