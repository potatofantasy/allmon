package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent can be used to calling OS Shell and retrieve various 
 * eligible from OS level metrics.<br><br>
 * 
 * Examples for windows:
 * <li> Get allmon task count (using: tasklist)
 * param1: "tasklist /V /FO csv /NH | find /c \"allmon\"" 
 * param2: "[0-9]+"
 * 
 * <li> ...
 * 
 */
public class ShellCallAgent extends ActiveAgent {
    
	private static final Log logger = LogFactory.getLog(ShellCallAgent.class);
    
	private String shellCommand = "ping google.com";
	private String searchPhrase = "";
	
	public ShellCallAgent(AgentContext agentContext) {
		super(agentContext);
	}
    
	public MetricMessageWrapper collectMetrics() {
	    String metricValue= "0";
        try {
			logger.debug("Executing shell command: [" + shellCommand + "]...");
			Process p = Runtime.getRuntime().exec(shellCommand);
	    	//p.waitFor();
			logger.debug("Shell command has been executed successfully.");
	        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//StringBuffer sb = new StringBuffer();
	        //int ch;
	        //while ((ch = in.read()) != -1) {
			//	sb.append((char) ch);
			//}
	        //logger.debug(sb.toString());
	        
	        //metricValue = OutputParser.findFirst(br, searchPhrase);
	        
	        OutputParser outputParser = new OutputParser(br);
	        metricValue = outputParser.findFirst(searchPhrase);
            
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} 
		//catch (InterruptedException e) {
		//	logger.error(e.getMessage(), e);
		//}
        
		MetricMessage message = MetricMessageFactory.createShellMessage(
				shellCommand, Long.parseLong(metricValue));
		
		return new MetricMessageWrapper(message);
	}
	
	void decodeAgentTaskableParams() {
        shellCommand = getParamsString(0);
        searchPhrase = getParamsString(1);
    }
    
	public void setShellCommand(String shellCommand) {
		this.shellCommand = shellCommand;
	}

	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}
	
}
