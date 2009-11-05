package org.allmon.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletRequest;

/**
 * This class is responsible for creating MetriMessage objects.
 * 
 */
public class MetricMessageFactory {
    
    private final static String HOSTNAME = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_HOST_NAME);
    private final static String INSTANCE = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_INSTANCE_NAME);
    
    /**
     * This object can be created only from this package.
     */
    MetricMessageFactory() {
    }
    
    public void validateMessage(MetricMessage metricMessage) throws MetricMessageInitializationException {
        if (metricMessage == null) {
            throw new MetricMessageInitializationException("MetricMessage is null");
        } else if (metricMessage.getPoint() == null || "".equals(metricMessage.getPoint())) {
            throw new MetricMessageInitializationException("MetricMessage point has not been initialized ");
        }
    }
    
    private static final MetricMessage createMessage(String artifact, String metricType) {
        MetricMessage metricMessage = new MetricMessage();
        if (!"".equals(HOSTNAME)) {
            metricMessage.setHost(HOSTNAME);
        }
        metricMessage.setInstance(INSTANCE);
        // TODO move to more OO design 
        metricMessage.setArtifact(artifact);
        metricMessage.setMetricType(metricType);
        return metricMessage;
    }
    
    public static final MetricMessage createActionClassMessage(String className, String user, String webSessionId, ServletRequest request) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ACTIONSERVLET);
        // resource - action class
        metricMessage.setResource(className);
        // source - user who triggered an action class to execute
        metricMessage.setSource(user);
        // session - is web session identifier
        metricMessage.setSession(webSessionId);
        //metricMessage.setDurationTime(durationTime);
        if (request != null && request.getParameterMap() != null) {
            metricMessage.setParameters(request.getParameterMap().toString());
        }
        return metricMessage;
    }

    // TODO review the hashing method !!!
    private String generateLogId(long startTime) {
        String sessionid = "" + Math.random(); //className + "-" + threadName + "-" + webSessionId + "-" + startTime + "-" + Math.random();
        // TODO add to StringBuffer and cache
        StringBuffer hexString = new StringBuffer();
        byte[] defaultBytes = sessionid.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            // String foo = messageDigest.toString();
            // System.out.println("sessionid " + sessionid + " md5 version is "
            // + hexString.toString());
            // sessionid = hexString + "";
        } catch (NoSuchAlgorithmException nsae) {

        }
        return hexString.toString();
    }

    public static final MetricMessage createClassMessage(String classNameCalled, String methodNameCalled, String classNameCalling, String methodNameCalling, long durationTime) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_JAVACLASS);
        // resource - class and method which has been called
        metricMessage.setResource(classNameCalled + "." + methodNameCalled);
        // source - user which class triggered an action class to execute
        metricMessage.setSource(classNameCalling + "." + methodNameCalling);
        metricMessage.setMetricValue(durationTime);
        return metricMessage;
    }

    public static final MetricMessage createPingMessage() {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_JAVACLASS); // TODO change type
        // resource - localhost name
        metricMessage.setResource(HOSTNAME);
        return metricMessage;
    }
    
    public static final MetricMessage createPingMessage(String pingedHost, long time) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_JAVACLASS); // TODO change type
        // resource - localhost name
        metricMessage.setResource(HOSTNAME);
        // source - hostname to which was sent ping
        metricMessage.setSource(pingedHost);
        metricMessage.setMetricValue(time);
        return metricMessage;
    }

    public static final MetricMessage createShellMessage(String command, long metricValue) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_SERVICELEVELCHECK);
        // resource - localhost name
        metricMessage.setResource(HOSTNAME);
        metricMessage.setSource(command);
        metricMessage.setMetricValue(metricValue);
        return metricMessage;
    }
    
    public static final MetricMessage createURLCallMessage(String checkName, String host, double metricValue) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_SERVICELEVELCHECK);
        metricMessage.setResource(checkName);
        metricMessage.setSource(host);
        metricMessage.setMetricValue(metricValue);
        return metricMessage;
    }
    
}
