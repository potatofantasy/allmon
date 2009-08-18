package org.allmon.common;

import java.io.Serializable;
import java.util.Date;

public class MetricMessage implements Serializable {

    private long eventTime;
    
    private long durationTime;
    
    private String host;
    
    private String instance; // TODO 
    
    private String thread; // TODO
    
    private String resource;
    
    private String source;
    
    private String session; // TODO add the session identifier to the allmetrics schema
    
    private Object parameters; // TODO check if possible List or Array!!!
    
    private String exception;

    public MetricMessage() {
        eventTime = System.currentTimeMillis();
        thread = Thread.currentThread().getName();
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public Object getParameters() {
        return parameters;
    }
    
    public String getParametersString() {
        return (parameters != null) ? parameters.toString() : "";
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("EventDateTime:");
        buffer.append(getEventDateTime());
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
