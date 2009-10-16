package org.allmon.client.agent;

import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent is responsible for calling OS Shell.
 * 
 */
public class ShellCallAgent extends ActiveAgent {
    
    private static final Log logger = LogFactory.getLog(ShellCallAgent.class);
    
	private String shellCommand = "cmd";
	
	public MetricMessage collectMetrics() {
	    // TODO Auto-generated method stub
        logger.debug("Shell command has been exeecuted.");
        return null;
	}
	
    public void setParameters(String[] paramsString) {
        // TODO Auto-generated method stub
        
    } 
	
}
