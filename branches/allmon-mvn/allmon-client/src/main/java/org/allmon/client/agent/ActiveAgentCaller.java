package org.allmon.client.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ActiveAgentCaller extends QuartzJobBean {

	private static final Log logger = LogFactory.getLog(ActiveAgentCaller.class);
	
	private String activeAgentClass;
	private ActiveAgentParameters parameters;
	private ActiveAgent activeAgent;

	public void setActiveAgentClass(String activeAgentClass) {
		this.activeAgentClass = activeAgentClass;
	}

	public void setParameters(ActiveAgentParameters parameters) {
		this.parameters = parameters;
	}
	
	public void setActiveAgent(ActiveAgent activeAgent) {
		this.activeAgent = activeAgent;
		logger.debug("Active agent set - " + activeAgent.getAgentContextName());
	}
	
	public void execute() {
		//logger.debug("Call active agent: " + activeAgentClass + " - " + parameters.getName());
		//logger.debug("activeAgent.execute() " + activeAgent.getClass() + "...");
		logger.debug("execute : " + activeAgent.getClass() + ".execute() ...");
		activeAgent.execute();
	}
	
	public void executeInternal(JobExecutionContext arg0) {
		execute();
		logger.debug("execute : " + activeAgent.getClass() + ".execute() finished");
	}

}
