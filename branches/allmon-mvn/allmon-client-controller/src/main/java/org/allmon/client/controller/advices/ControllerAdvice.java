package org.allmon.client.controller.advices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

abstract class ControllerAdvice {

//    static {
//        AllmonPropertiesReader.readLog4jProperties();
//    }

    private final Log logger = LogFactory.getLog(ControllerAdvice.class);
    
    private Controller controller;
    
	private String name;
	
	private boolean verboseMode = true; //AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_VERBOSELOGGING;
//	private boolean acquireCallParameters = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_ACQUIREPARAMETERS;
//	private boolean findCaller = AllmonCommonConstants.ALLMON_CLIENT_AGENT_ADVICES_FINDCALLER;
	
	public ControllerAdvice() {
		logger.info("Parameter - verboseMode: " + verboseMode);
//		logger.info("Parameter - acquireCallParameters: " + acquireCallParameters);
//		logger.info("Parameter - findCaller: " + findCaller);
	}
	
	abstract protected Object controll(ProceedingJoinPoint call) throws Throwable;
	
//	abstract protected MetricMessage createMetricMessage(JoinPoint call);
	
//	abstract protected AdvisableAgent createAgent(AgentContext agentContext, MetricMessage metricMessage);
	
	public void setController(Controller controller) {
		this.controller = controller;
	}

	public Controller getController() {
		return controller;
	}
	
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

//	public boolean isAcquireCallParameters() {
//		return acquireCallParameters;
//	}
//
//	public void setAcquireCallParameters(boolean acquireCallParameters) {
//		this.acquireCallParameters = acquireCallParameters;
//	}
//
//	public boolean isFindCaller() {
//		return findCaller;
//	}
//
//	public void setFindCaller(boolean findCaller) {
//		this.findCaller = findCaller;
//	}
	
}
