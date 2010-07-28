package org.allmon.common.jmxconfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AllmonAgentJmxParamsBean {

	private static final Log logger = LogFactory.getLog(AllmonAgentJmxParamsBean.class);
    
	private boolean isMonitoringOn = true;

	private boolean isSendingOn = true;
	
	public boolean isMonitoringOn() {
		if (!isMonitoringOn) {
			logger.debug("Metrics acquisition (monitoring) is switched off!");
		}
		return isMonitoringOn;
	}

	public void setMonitoringOn(boolean isMonitoringOn) {
		this.isMonitoringOn = isMonitoringOn;
	}

	public boolean isSendingOn() {
		if (!isSendingOn) {
			logger.debug("Metrics sending is switched off!");
		}
		return isSendingOn;
	}

	public void setSendingOn(boolean isSendingOn) {
		this.isSendingOn = isSendingOn;
	}
	
}
