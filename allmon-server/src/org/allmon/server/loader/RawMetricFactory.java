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
    		String host, String hostIp, String instance, String fullClassName, String user, long execTimeMS, long eventTime, String point, String parameters, String exception) {
    	RawMetric2 rawMetric = createMessage();
    	rawMetric.setArtifact(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION);
    	rawMetric.setHost(host);
    	rawMetric.setHostIp(hostIp);
    	rawMetric.setInstance(instance);
    	rawMetric.setMetricType(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_ACTIONSERVLET);
    	rawMetric.setResource(fullClassName);
    	rawMetric.setSource(user);
    	rawMetric.setMetric(new Double(execTimeMS));
    	// XXX time stamp value setting has been not finished yet
    	rawMetric.setTimeStamp(new Date(eventTime)); // TODO solve problem of time differences - on server side all times should be unified to avoid problems during analysis
    	rawMetric.setEntryPoint(point);
    	rawMetric.setParameters(parameters);
    	rawMetric.setException(exception);
    	
        return rawMetric;
    }
	
}
