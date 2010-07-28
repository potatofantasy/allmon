package org.allmon.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

/**
 * This class defines a data transformation object holding metrics data acquired in monitored application
 * by allmon client API, transformed (aggregated) and sent by allmon client to allmon server.
 * Allmon server decodes this object to RawMetric to persist this data in "raw" form in database.
 */
public class MetricMessage implements Serializable {

	private final static String HOSTNAME = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_HOST_NAME);
    private final static String INSTANCE = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_INSTANCE_NAME);
    	
    private static final int MAX_STRING_LENGHT = 1000;
    
    private static final XStream XSTREAM = new XStream(new JsonHierarchicalStreamDriver()); //new JettisonDriver());
    
    private static final boolean PRINT_EXCEPTIONS_STACKTRACE = true;
    
    private long eventTime = System.currentTimeMillis();
    private long durationTime;
    private double metricValue;
    private static final InetAddress addr = getInetAddress();
    private static final String hostIp = getIp(addr);

    private String artifact; // TODO move to more OO design and abstract creation factory 
    private String metricType; // TODO move to more OO design and abstract creation factory 

    private static final String host = (addr != null)?addr.getHostName():""; // HOSTNAME
    private String instance = INSTANCE;
    private String thread = Thread.currentThread().getName();
    private String resource;
    private String source;
    private String session; // TODO add the session identifier to the allmetric schema
    private String point = AllmonCommonConstants.METRIC_POINT_ENTRY; // by default all metrics are entry points 
    private Object parameters; // TODO check if possible use List or Array!!!
    private Exception exception;

    /**
     * MetricMessage objects can be created only by MetricMessageFactory.
     */
    MetricMessage() {
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
    
    private String trimAndCut(String s) {
        if (s != null) {
            s = s.trim();
            if (s.length() > MAX_STRING_LENGHT) {
                return s.substring(0, MAX_STRING_LENGHT);
            }
            return s;
        }
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

    public double getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }
    
    public double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    /**
     * @deprecated
     */
    public String getArtifact() {
        return artifact;
    }
    
    /**
     * @deprecated
     */
    void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getMetricType() {
        return metricType;
    }

    void setMetricType(String metricType) {
        this.metricType = metricType;
    }
    
    public String getHostIp() {
        return hostIp;
    }
    
    public String getHost() {
        return host;
    }
    
//    public void setHost(String host) {
//        this.host = trimAndCut(host);
//    }
    
    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = trimAndCut(instance);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = trimAndCut(resource);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = trimAndCut(source);
    }
    
    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        if (point == null || "".equals(point.trim())) {
            throw new RuntimeException("Point value cannot be null, blank or only white characters");
        }
        this.point = point;
    }
        
    public Object getParameters() {
        return parameters;
    }
    
    public String getParametersString() {
        if (parameters != null ) {
            return XSTREAM.toXML(parameters);
        }
        return "";
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public Exception getException() {
        return exception;
    }
    
    public String getExceptionString() {
        if (exception == null) {
            return "";
        }
        if (PRINT_EXCEPTIONS_STACKTRACE) {
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            exception.printStackTrace(printWriter);
            return result.toString();
        }
        return exception.toString();
    }

    // TODO change to throwable
    public void setException(Exception exception) {
        this.exception = exception;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Host:");
        buffer.append(getHost());
        buffer.append("(");
        buffer.append(getHostIp());
        buffer.append(")");
        buffer.append(", Instance:");
        buffer.append(getInstance());
        buffer.append(", MetricType:");
        buffer.append(getMetricType());        
        buffer.append(", Resource:");
        buffer.append(getResource());
        buffer.append(", Source:");
        buffer.append(getSource());
        buffer.append(", MetricValue:");
        buffer.append(metricValue);
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
    
    // TODO evaluate implementing deep cloning
    public MetricMessage clone() {
        try {
            return cloneX(this);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private static <T> T cloneX(T x) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream cout = new ObjectOutputStream(bout);
        cout.writeObject(x);
        byte[] bytes = bout.toByteArray();

        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream cin = new ObjectInputStream(bin);

        @SuppressWarnings("unchecked")
        T clone = (T) cin.readObject();
        return clone;
    }

    
}
