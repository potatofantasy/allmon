package org.allmon.common.jmxconfig;

public class AllmonAgentJmxParamsBean {

	private boolean isMonitoringOn = true;

	private boolean isSendingOn = true;
	
	public boolean isMonitoringOn() {
		return isMonitoringOn;
	}

	public void setMonitoringOn(boolean isMonitoringOn) {
		this.isMonitoringOn = isMonitoringOn;
	}

	public boolean isSendingOn() {
		return isSendingOn;
	}

	public void setSendingOn(boolean isSendingOn) {
		this.isSendingOn = isSendingOn;
	}
	
}
