package org.allmon.client.agent;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent is responsible for calling OS Shell
 * 
 */
public class ShellCallAgent extends ActiveAgent {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
	
    private static final Log logger = LogFactory.getLog(ShellCallAgent.class);

	private String shellCommand = "cmd";
	
	public void execute() {
		// TODO Auto-generated method stub
		logger.debug("Shell command has been exeecuted.");
	} 
	
}
