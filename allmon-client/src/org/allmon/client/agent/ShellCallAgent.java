package org.allmon.client.agent;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent is responsible for calling OS Shell.
 * 
 */
public class ShellCallAgent extends ActiveAgent {
    
    private static final Log logger = LogFactory.getLog(ShellCallAgent.class);
    
	private String shellCommand = "ping google.com";
	private String searchPhrase = "";
	
	public MetricMessage collectMetrics() {
	    String metricValue= "0";
        Process p;
		try {
			logger.debug("Executing shell command: [" + shellCommand + "]...");
	        p = Runtime.getRuntime().exec(shellCommand);
			logger.debug("Shell command has been exeecuted successfully.");
	        InputStream in = p.getInputStream();
	        StringBuffer sb = new StringBuffer();
	        //int ch;
	        //while ((ch = in.read()) != -1) {
			//	sb.append((char) ch);
			//}

	        logger.debug(sb.toString());
	        
	        metricValue = OutputParser.findFirst((DataInputStream)in, searchPhrase);
	        
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
        
		MetricMessageFactory.createShellMessage(shellCommand, Long.parseLong(metricValue));
		
        return null;
	}
	
    public void setParameters(String[] paramsString) {
        if (paramsString != null && paramsString.length >= 2) {
        	shellCommand = paramsString[0];
            searchPhrase = paramsString[1];        
        }
    }

	public void setShellCommand(String shellCommand) {
		this.shellCommand = shellCommand;
	}

	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}
	
}
