package org.allmon.client.agent.snmp;

import org.snmp4j.mp.SnmpConstants;

public class SNMPSettings {
    private String readCommunity = "public";
    private String writeCommunity = "private";
    private int SnmpVersion =  SnmpConstants.version1;
    private int SnmpPortRes = SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT;
    private int SnmpPortRcv = SnmpConstants.DEFAULT_NOTIFICATION_RECEIVER_PORT;    
    private String IPAddress = "127.0.0.1";    
    private long timeout = 1000;
    
	public String getReadCommunity() {
		return readCommunity;
	}
	public void setReadCommunity(String readCommunity) {
		this.readCommunity = readCommunity;
	}
	public String getWriteCommunity() {
		return writeCommunity;
	}
	public void setWriteCommunity(String writeCommunity) {
		this.writeCommunity = writeCommunity;
	}
	public int getSnmpVersion() {
		return SnmpVersion;
	}
	public void setSnmpVersion(int snmpVersion) {
		SnmpVersion = snmpVersion;
	}

	public String getIPAddress() {
		return IPAddress;
	}
	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}
	public int getSnmpPortRes() {
		return SnmpPortRes;
	}
	public void setSnmpPortRes(int snmpPortRes) {
		SnmpPortRes = snmpPortRes;
	}
	public int getSnmpPortRcv() {
		return SnmpPortRcv;
	}
	public void setSnmpPortRcv(int snmpPortRcv) {
		SnmpPortRcv = snmpPortRcv;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
