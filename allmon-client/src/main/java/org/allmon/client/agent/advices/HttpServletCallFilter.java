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
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

/**
 * This is servlet filter implementation used to gather run-time metrics.
 * 
 *    <init-param>
 *         <param-name>org.allmon.client.agent.advices.HttpServletCallFilter.filterName</param-name>
 *         <param-value>ExampleFilter1</param-value>
 *    </init-param>
 *	  <init-param>
 *         <param-name>org.allmon.client.agent.advices.HttpServletCallFilter.userAttributeKey</param-name>
 *         <param-value>UserID</param-value>
 *    </init-param>
 * 
 */
public class HttpServletCallFilter implements Filter { // TODO review: extends AllmonAdvice 

    private FilterConfig filterConfig;
    
    private AgentContext agentContext;

    private String filterName;
    private String userAttributeKey;
	    
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        // for every filter instance one allmon agent context object is created 
        agentContext = new AgentContext();
        // set filter parameters
        filterName = filterConfig.getInitParameter(getClass().getName() + ".filterName");
        userAttributeKey = filterConfig.getInitParameter(getClass().getName() + ".userAttributeKey");
    }

    public void destroy() {
        this.filterConfig = null;
        agentContext.stop();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws ServletException {
        if (filterConfig == null)
            return;

        // TODO finish acquiring user id from session
        // - attribute key name is needed passed by filter parameter
        String user = "";
        if (!"".equals(userAttributeKey)) {
        	try {
        		user = ((HttpServletRequest)request).getSession().getAttribute(userAttributeKey).toString(); 
        	} catch (Exception ex) {
        		user = "user-not-found";
        	}
        }
        
        // start 
        MetricMessage message = MetricMessageFactory.createServletMessage(
        		filterName, (HttpServletRequest) request, user);

        JavaCallAgent agent = new JavaCallAgent(agentContext, message);
        agent.entryPoint();

        Exception e = null;
		try {
            chain.doFilter(request, response);

            // TODO log responses codes
            
            // TODO log http errors codes
            
        } catch (Exception ex) {
            e = ex;
        } finally {
        	agent.exitPoint(e);
        }
        
    }

}