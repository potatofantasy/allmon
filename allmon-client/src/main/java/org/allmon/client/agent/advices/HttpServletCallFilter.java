package org.allmon.client.agent.advices;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is servlet filter implementation is used to gather selected servlets run-time metrics.
 * 
 * Filter parameters:
 * - filterName - filter name identifies the filter definition in metrics data, can be also seen in logging 
 * - sessionUserAttributeKey - a key string which points at user data object stored in http session (by default name/id is acquired by calling toString() method)
 * - serializeUserObject - if true taken from session user object will be serialized in JSON format, otherwise to get user details method toString will be called on the object
 * - captureRequest - this flag defines weather request object will be stored in metric data or not
 * 
 * Below snippet from /WEB-INF/web.xml file contains an example of a monitoring filter 
 * which captures all elements (all urls) of a web application.
 * 
 *    <filter>
 *        <filter-name>PerformanceMonitoringFilter</filter-name>
 *        <filter-class>org.allmon.client.agent.advices.HttpServletCallFilter</filter-class>
 *        <init-param>
 *            <param-name>org.allmon.client.agent.advices.HttpServletCallFilter.filterName</param-name>
 *            <param-value>ExampleFilter1</param-value>
 *        </init-param>
 *        <init-param>
 *	        <param-name>org.allmon.client.agent.advices.HttpServletCallFilter.sessionUserAttributeKey</param-name>
 *	        <param-value></param-value>
 *        </init-param>
 *        <init-param>
 *	        <param-name>org.allmon.client.agent.advices.HttpServletCallFilter.serializeUserObject</param-name>
 *	        <param-value>true</param-value>
 *        </init-param>
 *        <init-param>
 *	        <param-name>org.allmon.client.agent.advices.HttpServletCallFilter.captureRequest</param-name>
 *	        <param-value>true</param-value>
 *        </init-param>
 *    </filter>
 *    <filter-mapping>
 *        <filter-name>PerformanceMonitoringFilter</filter-name>
 *        <url-pattern>*</url-pattern>
 *    </filter-mapping>
 * 
 */
public class HttpServletCallFilter implements Filter {

	private static final Log logger = LogFactory.getLog(HttpServletCallFilter.class);
		
//	private boolean verboseMode = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_VERBOSELOGGING;
	private boolean acquireCallParameters = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_ACQUIREPARAMETERS;
//	private boolean findCaller = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_FINDCALLER;
		
    private FilterConfig filterConfig;
    
    private AgentContext agentContext;

    private String filterName;
    private String sessionUserAttributeKey;
    private boolean serializeUserObject;
    private boolean captureRequest;
    
	    
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        // for every filter instance one allmon agent context object is created 
        agentContext = new AgentContext();
        // set filter parameters
        filterName = filterConfig.getInitParameter(getClass().getName() + ".filterName");
        sessionUserAttributeKey = filterConfig.getInitParameter(getClass().getName() + ".sessionUserAttributeKey");
        serializeUserObject = Boolean.getBoolean(filterConfig.getInitParameter(getClass().getName() + ".serializeUserObject"));
        captureRequest = Boolean.getBoolean(filterConfig.getInitParameter(getClass().getName() + ".captureRequest"));
        
        logger.info("HttpServletCallFilter has been initialized; name: " + filterName + ", sessionUserAttributeKey: " + sessionUserAttributeKey + ", captureRequest: " + captureRequest);
    }

    public void destroy() {
        this.filterConfig = null;
        agentContext.stop();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws ServletException {
        if (filterConfig == null)
            return;
        
        // get user id from session
        String user = "";
        if (!"".equals(sessionUserAttributeKey)) {
        	try {
        		HttpServletRequestUtil util = new HttpServletRequestUtil((HttpServletRequest)request);
        		if (serializeUserObject) {
        			user = util.getUserObjectSerializedString(sessionUserAttributeKey);
        		} else {
            		user = util.getUserObjectString(sessionUserAttributeKey);
        		}
        	} catch (Exception ex) {
        		user = "user-not-found";
        	}
        }
        
        // create a metric message
        MetricMessage message = MetricMessageFactory.createServletMessage(
        		filterName, (HttpServletRequest)request, user);
        
        // get request object as a set of the call parameters
        if (acquireCallParameters && captureRequest) {
        	// ServletReques is not serializable so it will be converted to json strings 
            // before it can be sent further
        	message.setParameters(request);
        }
        
        JavaCallAgent agent = new JavaCallAgent(agentContext, message);
        agent.entryPoint();
        
        Throwable t = null;
		try {
            chain.doFilter(request, response);

            // TODO evaluate storing responses codes
            
            // TODO log http errors codes
            
        } catch (Throwable th) {
            t = th;
        } finally {
        	agent.exitPoint(t);
        }
        
    }

}