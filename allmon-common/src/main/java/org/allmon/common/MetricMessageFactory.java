package org.allmon.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is responsible for creating MetriMessage objects.
 * 
 */
public class MetricMessageFactory {
    
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
    
    // TODO add resource as a mandatory field
    private static final MetricMessage createMessage(String artifact, String metricType) {
        MetricMessage metricMessage = new MetricMessage();
        // TODO move to more OO design 
        metricMessage.setArtifact(artifact);
        metricMessage.setMetricType(metricType);
        //metricMessage.setPoint(AllmonCommonConstants.METRIC_POINT_ENTRY); // TODO review necessity of this setter
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
        metricMessage.setSession(webSessionId); // TODO take websessionid from request
        // parameters // TODO review 
        if (request != null && request.getParameterMap() != null) {
            metricMessage.setParameters(request.getParameterMap().toString());
        }
        return metricMessage;
    }
    
    public static final MetricMessage createServletMessage(String filterName, HttpServletRequest request, String user) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ACTIONSERVLET);
        if (request == null) {
        	throw new NullPointerException("HttpServletRequest object is nul");
        }
        // resource - url
        metricMessage.setResource(new StringBuffer(
        		filterName).append("//").append(
        		request.getProtocol()).append("://").append(
        		request.getLocalAddr()).append("(").append(
				request.getLocalName()).append("):").append(
				request.getLocalPort()).append("/").append(
				request.getRequestURI()).append(" [").append(
				request.getContentType()).append("]").toString());
        // source - machine which triggered a servlet to execute
        metricMessage.setSource(new StringBuffer(
        		request.getRemoteHost()).append(":").append(
        		request.getRemotePort()).toString());
        // session - is web session identifier
        metricMessage.setSession(new StringBuffer(
        		user).append(":").append(
        		request.getRequestedSessionId()).toString());
        // parameters
        if (request.getParameterMap() != null) {
            metricMessage.setParameters(request.getParameterMap()); // TODO review conversion to Array
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

    public static final MetricMessage createClassMessage(String classNameCalled, String methodNameCalled, String classNameCalling, String methodNameCalling) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_JAVACLASS);
        // resource - class and method which has been called
        metricMessage.setResource(classNameCalled + "." + methodNameCalled);
        // source - user which class triggered an action class to execute
        if (classNameCalling != null && !"".equals(classNameCalling)) {
        	metricMessage.setSource(classNameCalling + "." + methodNameCalling);
        }
        metricMessage.setMetricValue(0); //durationTime);
        return metricMessage;
    }

    public static final MetricMessage createPingMessage(String activeAgentName) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ALLMON_HEARTBEAT);
        // resource - localhost name
        metricMessage.setResource(activeAgentName);
        metricMessage.setMetricValue(1);
        return metricMessage;
    }
    
    public static final MetricMessage createPingMessage(String activeAgentName, String pingedHost, long time, Throwable throwable) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_ALLMON_HEARTBEAT);
        // resource - localhost name
        metricMessage.setResource(activeAgentName);
        // source - hostname to which was sent ping
        metricMessage.setSource(pingedHost);
        metricMessage.setMetricValue(time);
        metricMessage.setThrowable(throwable);
        return metricMessage;
    }

    public static final MetricMessage createShellMessage(String command, long metricValue) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_SERVICELEVELCHECK);
        // resource - localhost name
        metricMessage.setSource(command);
        metricMessage.setMetricValue(metricValue);
        return metricMessage;
    }
    
    /**
     * 
     * @param checkName name of resource/health check which is monitored
     * @param instanceName monitored web application/service instance name
     * @param host monitored host (source)
     * @param metricValue
     * @param exception
     * @return
     */
    public static final MetricMessage createUrlCallMessage(String checkName, String instanceName, String host, double metricValue, Throwable throwable) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_APPLICATION,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_APP_SERVICELEVELCHECK);
        metricMessage.setInstance(instanceName);
        metricMessage.setResource(checkName);
        metricMessage.setSource(host);
        metricMessage.setMetricValue(metricValue);
        metricMessage.setThrowable(throwable);
        return metricMessage;
    }

    public static final MetricMessage createJmxMessage(
            long jvmId, String jvmName, 
            String mbeanFullName, String mbeanAttributeName,
            double metricValue, Exception exception) {
        MetricMessage metricMessage = createJmxMessage(
                jvmName.split(" ")[0], // + ":id=" + jvmId,
                mbeanFullName, mbeanAttributeName,
                metricValue, exception);
        return metricMessage;
    }
    public static final MetricMessage createJmxMessage(
            String jvmNameId, String mbeanFullName, String mbeanAttributeName,
            double metricValue, Throwable throwable) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_JVM,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_JVM_JMX); // general JMX
        metricMessage.setInstance(jvmNameId);
        metricMessage.setResource(mbeanFullName + ":" + mbeanAttributeName);
        //metricMessage.setSource();
        metricMessage.setMetricValue(metricValue);
        metricMessage.setThrowable(throwable);
        return metricMessage;
    }
    public static final MetricMessage createJmxMessageAgentVM(
            String mbeanFullName, String mbeanAttributeName,
            double metricValue, Throwable throwable) {
        MetricMessage metricMessage = createMessage(
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_JVM,
                AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_METRICTYPE_JVM_JMX); // general JMX
        //metricMessage.setInstance(jvmNameId);
        metricMessage.setResource(mbeanFullName + ":" + mbeanAttributeName);
        //metricMessage.setSource();
        metricMessage.setMetricValue(metricValue);
        metricMessage.setThrowable(throwable);
        return metricMessage;
    }
    
	public static final MetricMessage createSnmpOSMessage(String metricType, String resource, double metricValue,
			Throwable throwable) {
		MetricMessage metricMessage = createMessage(
				AllmonCommonConstants.ALLMON_SERVER_RAWMETRIC_ARTIFACT_OS, 
				metricType);
		metricMessage.setResource(resource);
		metricMessage.setMetricValue(metricValue);
		metricMessage.setThrowable(throwable);
		metricMessage.setSource(null);
		return metricMessage;
	}
    
}