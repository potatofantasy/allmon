package org.allmon.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletRequest;


public class MetricMessageFactory {
    
    private final static String HOSTNAME = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_HOST_NAME);
    private final static String INSTANCE = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_INSTANCE_NAME);
    
    MetricMessageFactory() {
    }
        
    private static final MetricMessage createMessage() {
        MetricMessage metricMessage = new MetricMessage();
        if (!"".equals(HOSTNAME)) {
            metricMessage.setHost(HOSTNAME);
        }
        metricMessage.setInstance(INSTANCE);
        return metricMessage;
    }
    
    public static final MetricMessage createActionClassMessage(String className, String user, String webSessionId, ServletRequest request) {
        MetricMessage metricMessage = createMessage();
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

    public static final MetricMessage createClassMessage(String className, String methodName, String user, long durationTime) {
        MetricMessage metricMessage = createMessage();
        // resource - class and method
        metricMessage.setResource(className + "." + methodName);
        // source - user who triggered an action class to execute
        metricMessage.setSource(user);
        metricMessage.setMetricValue(durationTime);
        return metricMessage;
    }

    public static final MetricMessage createPingMessage() {
        MetricMessage metricMessage = createMessage();
        // resource - localhost name
        metricMessage.setResource(HOSTNAME);
        return metricMessage;
    }
    
    public static final MetricMessage createPingMessage(String pingedHost, long time) {
        MetricMessage metricMessage = createMessage();
        // resource - localhost name
        metricMessage.setResource(HOSTNAME);
        // source - hostname to which was sent ping
        metricMessage.setSource(pingedHost);
        metricMessage.setMetricValue(time);
        return metricMessage;
    }

    public static final MetricMessage createShellMessage(String command, long metricValue) {
        MetricMessage metricMessage = createMessage();
        // resource - localhost name
        metricMessage.setResource(HOSTNAME);
        metricMessage.setSource(command);
        metricMessage.setMetricValue(metricValue);
        return metricMessage;
    }
    
    public static final MetricMessage createURLCallMessage(String url, String searchPhrase, double metricValue) {
        MetricMessage metricMessage = createMessage();
        metricMessage.setResource(url);
        metricMessage.setSource(searchPhrase);
        metricMessage.setMetricValue(metricValue);
        return metricMessage;
    }
    
}
