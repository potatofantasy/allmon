package org.allmon.common;

import static org.allmon.common.AllmonPropertiesReader.getInstance;

import org.apache.activemq.ActiveMQConnection;

public class AllmonCommonConstants {

    public final static String METRIC_POINT_ENTRY = "ENTRY";
    public final static String METRIC_POINT_EXIT = "EXIT";
    public final static String METRIC_POINT_TAKEN = "TAKEN";
    public final static String METRIC_POINT_CONSUMED = "CONSUMED";
    public final static String METRIC_POINT_NONE = "NONE";
    
    
    // settings for agents
    // metric buffer settings
    public final static long ALLMON_CLIENT_AGENT_METRICBUFFER_FLUSHINGINTERVAL = getInstance().getValueInt("allmon.client.agent.metricbuffer.flushinginterval", 2000);
    public final static boolean ALLMON_CLIENT_AGENT_METRICBUFFER_VERBOSELOGGING = getInstance().getValueBoolean("allmon.client.agent.metricbuffer.verboselogging", false);
  
    // client agent proxy setting
    public final static boolean ALLMON_CLIENT_AGENT_PROXY_ACTIVE = Boolean.parseBoolean(getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_AGENT_PROXY_ACTIVE));
    public final static String ALLMON_CLIENT_AGENT_PROXY_HOST = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_AGENT_PROXY_HOST);
    public final static String ALLMON_CLIENT_AGENT_PROXY_PORT = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_AGENT_PROXY_PORT);
    public final static String ALLMON_CLIENT_AGENT_PROXY_USERNAME = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_AGENT_PROXY_USERNAME);
    public final static String ALLMON_CLIENT_AGENT_PROXY_PASSWORD = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_AGENT_PROXY_PASSWORD);

    // client active agent 
    public final static String ALLMON_CLIENT_AGENT_ACTIVEAGENT_APPCONTEXT_CONF_FILENAME = getInstance().getValue("allmon.client.agent.activeagent.appcontext.conf.filename", "activeAgentAppContext.xml");

    public final static boolean ALLMON_CLIENT_AGENT_JMXSERVERAGENT_VERBOSELOGGING = getInstance().getValueBoolean("allmon.client.agent.jmxserveragent.verboselogging", false);
    
    // client passive agent advices
    public final static boolean ALLMON_CLIENT_AGENT_ADVICES_VERBOSELOGGING = getInstance().getValueBoolean("org.allmon.client.agent.advices.allmonadvice.verboseLogging", false);
    public final static boolean ALLMON_CLIENT_AGENT_ADVICES_ACQUIREPARAMETERS = getInstance().getValueBoolean("org.allmon.client.agent.advices.allmonadvice.acquireCallParameters", false);
    public final static boolean ALLMON_CLIENT_AGENT_ADVICES_FINDCALLER = getInstance().getValueBoolean("org.allmon.client.agent.advices.allmonadvice.findCaller", false);

    // settings for connection to activemq jms broker
    public final static String CLIENT_BROKER_USER = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_USER, ActiveMQConnection.DEFAULT_USER);
    public final static String CLIENT_BROKER_PASSWORD = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
    public final static String CLIENT_BROKER_URL = getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
    public final static String SERVER_BROKER_USER = getInstance().getValue(AllmonPropertiesConstants.ALLMON_SERVER_BROKER_USER, ActiveMQConnection.DEFAULT_USER);
    public final static String SERVER_BROKER_PASSWORD = getInstance().getValue(AllmonPropertiesConstants.ALLMON_SERVER_BROKER_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
    public final static String SERVER_BROKER_URL = getInstance().getValue(AllmonPropertiesConstants.ALLMON_SERVER_BROKER_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
    
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
    public final static boolean ALLMON_CLIENT_AGGREGATOR_VERBOSELOGGING = getInstance().getValueBoolean("allmon.client.aggregator.verboselogging", false);
    public final static int ALLMON_CLIENT_AGGREGATOR_BATCHSIZE = getInstance().getValueInt(AllmonPropertiesConstants.ALLMON_CLIENT_AGGREGATOR_BATCHSIZE, 100);
    public final static long ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT = getInstance().getValueInt(AllmonPropertiesConstants.ALLMON_CLIENT_AGGREGATOR_BATCHTIMEOUT, 10 * 1000);
    
    // for local agents
    public final static long ALLMON_CLIENT_BROKER_HEALTH_SAMPLER_HEARTBEATRATE = getInstance().getValueInt(AllmonPropertiesConstants.ALLMON_CLIENT_BROKER_HEALTH_SAMPLER_HEARTBEATRATE, 30 * 1000);
    
    // for allmon-server loader - TODO review moving those constants to separate class visible only for allmon-server 
    // all values must be in sync with values defined in allmon database in static dimensions
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_OS = "OS"; //"operating.system";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION = "APP"; //"application"; 
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_REPORT = "REP"; //"report";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_JVM = "JVM"; //"java";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_DB = "DB"; //"database";
    public final static String ALLMON_SERVER_RAWMETRIC_ARTIFACT_HARDWARE = "HW"; //"hardware";
    
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ACTIONSERVLET = "ACTCLS"; //"application.action.servlet"; 
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_JAVACLASS = "JAVCLS"; //"application.action.?"; 
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_SERVICELEVELCHECK = "APPSLC"; //"application.slc.?"; 
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ALLMON_HEARTBEAT = "APPAHB"; //"application.slc.allmon.heartbeat"; 
    
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_JVM_JMX = "JVMJMX"; //"java.jmx.?";
    
    // Snmp host metrics:
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_CPULOAD = "CPULOAD";
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_PROCESS_CPU_TIME = "PROCESS_CPU_TIME";
    public final static String ALLMON_SERVER_RAWMETRIC_METRICTYPE_OS_PROCESS_MEMORY = "PROCESS_MEM";
    
    
}