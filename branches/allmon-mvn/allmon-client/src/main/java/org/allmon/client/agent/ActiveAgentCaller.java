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
		System.out.println("Active agent set!!!" + activeAgent.getAgentContextName());
	}
	
	public void execute() {
		System.out.println("execute!!!");
		//System.out.println("Call active agent: " + activeAgentClass + " - " + parameters.getName()); // TODO move to log4j
		
		//System.out.println("activeAgent.execute() " + activeAgent.getClass() + "...");
		
		//logger.debug("Execution : " + activeAgent.getClass() + ".execute() ...");
		activeAgent.execute();
		
	}
	
	@Override
	public void executeInternal(JobExecutionContext arg0) {
		execute();
		//logger.debug("Execution : " + activeAgent.getClass() + ".execute() finished");
	}

}
