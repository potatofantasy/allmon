package org.allmon.server.loader;

import java.sql.Date;

public class RawMetricFactory {

	private static final RawMetric2 createMessage() {
		RawMetric2 rawMetric = new RawMetric2();
        // TODO add common logic here 
        return rawMetric;
    }
    
    public static final RawMetric2 createRawMetric(
            String artifact, String metricType,
    		String host, String hostIp, String instance, String fullClassName, String source, String session, String thread, long execTimeMS, long eventTime, String point, String parameters, String exception) {
    	RawMetric2 rawMetric = createMessage();
    	//rawMetric.setArtifact(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION);
    	rawMetric.setArtifact(artifact); // TODO move to proper OO structure 
        rawMetric.setHost(host);
    	rawMetric.setHostIp(hostIp);
    	rawMetric.setInstance(instance);
    	//rawMetric.setMetricType(AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ACTIONSERVLET);
    	rawMetric.setMetricType(metricType); // TODO move to proper OO structure 
        rawMetric.setResource(fullClassName);
    	rawMetric.setSource(source);
    	rawMetric.setSession(session);
    	rawMetric.setThread(thread);
    	rawMetric.setMetric(new Double(execTimeMS));
    	// XXX time stamp value setting has been not finished yet
    	rawMetric.setTimeStamp(new Date(eventTime)); // TODO solve problem of time differences - on server side all times should be unified to avoid problems during analysis
    	rawMetric.setEntryPoint(point);
    	rawMetric.setParameters(parameters);
    	rawMetric.setException(exception);
        return rawMetric;
    }
	
}
