package org.allmon.client.agent.advices;

import org.allmon.client.agent.AgentContext;
import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

abstract class AllmonAdvice {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    private final Log logger = LogFactory.getLog(AllmonAdvice.class);
    
	AgentContext agentContext;
    
	private String name;
	
	private boolean verboseMode = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_VERBOSELOGGING;
	private boolean acquireCallParameters = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_ACQUIREPARAMETERS;
	private boolean findCaller = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_FINDCALLER;
	
	public AllmonAdvice() {
		logger.info("Parameter - verboseMode: " + verboseMode);
		logger.info("Parameter - acquireCallParameters: " + acquireCallParameters);
		logger.info("Parameter - findCaller: " + findCaller);
	}	
	
	abstract protected Object profile(ProceedingJoinPoint call) throws Throwable;
	
	abstract protected MetricMessage createMetricMessage(JoinPoint call);
	
	abstract protected AdvisableAgent createAgent(AgentContext agentContext, MetricMessage metricMessage);
		
	public void setAgentContext(AgentContext agentContext) {
		this.agentContext = agentContext;
	}
	
//	protected AgentContext getAgentContext() {
//		return agentContext;
//	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isVerboseMode() {
		return verboseMode;
	}
	
	public void setVerboseMode(boolean verboseMode) {
		this.verboseMode = verboseMode;
	}

	public boolean isAcquireCallParameters() {
		return acquireCallParameters;
	}

	public void setAcquireCallParameters(boolean acquireCallParameters) {
		this.acquireCallParameters = acquireCallParameters;
	}

	public boolean isFindCaller() {
		return findCaller;
	}

	public void setFindCaller(boolean findCaller) {
		this.findCaller = findCaller;
	}
	
}
