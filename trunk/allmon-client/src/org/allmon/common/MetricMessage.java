package org.allmon.common;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * This class defines a data transformation object holding metrics data acquired in monitored application
 * by allmon client API, transformed (aggregated) and sent by allmon client to allmon server.
 * Allmon server decodes this object to RawMetric to persist this data in "raw" form in database.
 */
public class MetricMessage implements Serializable {

    private long eventTime;
    private long durationTime;
    private static final InetAddress addr = getInetAddress();
    private static final String hostIp = getIp(addr);
    private String host;
    private String instance;
    private String thread;
    private String resource;
    private String source;
    private String session; // TODO add the session identifier to the allmetric schema
    private String point; 
    private Object parameters; // TODO check if possible use List or Array!!!
    private Exception exception;

    public MetricMessage() {
    	if (addr != null) {
    		host = addr.getHostName();
    	} else {
    		host = "";
    	}
        eventTime = System.currentTimeMillis();
        thread = Thread.currentThread().getName();
    }
    
    private static InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
        }
        return null;
    }
    
    private static String getIp(InetAddress inetAddress) {
        if (inetAddress != null)
            return inetAddress.getHostAddress();
        return "";
    }
    
    public long getEventTime() {
        return eventTime;
    }

    public Date getEventDateTime() {
        return new Date(eventTime);
    }
    
//    public void setEventTime(long eventTime) {
//        this.eventTime = eventTime;
//    }

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }
    
    public String getHostIp() {
        return hostIp;
    }

    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }

    public String getInstance() {
        return instance;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
        
    public Object getParameters() {
        return parameters;
    }
    
    public String getParametersString() {
        return (parameters != null) ? parameters.toString() : "";
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public Exception getException() {
        return exception;
    }
    
    public String getExceptionString() {
        return (exception != null) ? exception.toString() : "";
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Host:");
        buffer.append(getHost());
        buffer.append("(");
        buffer.append(getHostIp());
        buffer.append(") ");
        buffer.append(", Instance:");
        buffer.append(getInstance());
        buffer.append(", Resource:");
        buffer.append(getResource());
        buffer.append(", Source:");
        buffer.append(getSource());
        buffer.append(", DurationTime(ms):");
        buffer.append(durationTime);
        buffer.append(", Parameters:");
        buffer.append(getParametersString());
        buffer.append(", Exception:");
        buffer.append(exception);
        return buffer.toString();
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

}
