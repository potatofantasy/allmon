package org.allmon.server.loader;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "AM_RAW_METRIC")
public class RawMetric2 implements Serializable {

	private Long id = new Long(-1);
    
	private String artifact;
	
	private String host;
	
	private String hostIp;
    
	private String instance;
	
	private String metricType;
	
	private String resource;
	
	private String source;
	
	private Double metric;
	
	private java.util.Date timeStamp;
	
	// @Column(nullable=false)
    // private String Time // TODO check sense of this item 
	
	private String parameters; //Object parameters; // TODO check if possible use List or Array!!!
	
    private String exception; //Exception exception; 
	
// items from MetricMessage    
//    private long eventTime;
//    private long durationTime;
//    private static final InetAddress addr = getInetAddress();
//    private static final String hostIp = getIp(addr);
//    private String host;
//    private String instance;
//    private String thread;
//    private String resource;
//    private String source;
//    private String session; // TODO add the session identifier to the allmetrics schema
//    private Object parameters; // TODO check if possible use List or Array!!!
//    private Exception exception;
    
//    @Column(nullable=false, length=2000)
//    private String metric;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="AM_RME_ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="ARTIFACTCODE", nullable=false, length=100)
    public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

    @Column(name="HOSTNAME", nullable=false, length=100)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Column(name="HOSTIP", nullable=false, length=100)
    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
    
    @Column(name="INSTANCENAME", nullable=false, length=100)
    public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	@Column(name="METRICTYPECODE", nullable=true, length=100)
    public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	@Column(name="RESOURCENAME", nullable=true, length=100)
    public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	@Column(name="SOURCENAME", nullable=true, length=100)
    public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name="METRICVALUE", nullable=false, length=16, precision=6)
    public Double getMetric() {
		return metric;
	}

	public void setMetric(Double metric) {
		this.metric = metric;
	}

	@Column(name="TS", nullable=false)
    public java.util.Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(java.util.Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Column(name="PARAMETERS", nullable=true, length=1000)
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@Column(name="EXCEPTION", nullable=true, length=1000)
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}
