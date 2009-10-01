package org.allmon.server.loader;

import java.sql.Date;

import org.allmon.common.AllmonCommonConstants;

public class RawMetricFactory {

	private static final RawMetric2 createMessage() {
		RawMetric2 rawMetric = new RawMetric2();
        // TODO add common logic here 
        return rawMetric;
    }
    
    public static final RawMetric2 createApplicationActionServletRawMetric(
    		String host, String instance, String fullClassName, String user, long execTimeMS, long eventTime, String parameters, String exception) {
    	RawMetric2 rawMetric = createMessage();
    	rawMetric.setArtifact(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION);
    	rawMetric.setHost(host);
    	rawMetric.setInstance(instance);
    	rawMetric.setMetricType(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_ACTIONSERVLET);
    	rawMetric.setResource(fullClassName);
    	rawMetric.setSource(user);
    	rawMetric.setMetric(execTimeMS);
    	// XXX time stamp value setting has been not finished s
    	//rawMetric.setTimeStamp(eventTime); // TODO solve problemoftime differences - on server side all times should be unified to avoid problems during analysis
    	rawMetric.setParameters(parameters);
    	rawMetric.setException(exception);
    	
//		private long eventTime;
//	    private long durationTime;
//	    private static final InetAddress addr = getInetAddress();
//	    private static final String hostIp = getIp(addr);
//	    private String host;
//	    private String instance;
//	    private String thread;
//	    private String resource;
//	    private String source;
//	    private String session; // TODO add the session identifier to the allmetrics schema
//	    private Object parameters; // TODO check if possible use List or Array!!!
//	    private Exception exception;
    	
        // resource - action class
        //metricMessage.setResource(className);
        // source - user who triggered an action class to execute
        //metricMessage.setSource(user);
        // session - is web session identifier
        //metricMessage.setSession(webSessionId);
        //metricMessage.setDurationTime(durationTime);
        //if (request != null && request.getParameterMap() != null) {
        //    metricMessage.setParameters(request.getParameterMap().toString());
        //}
        return rawMetric;
    }
	
}
