package org.allmon.loader;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "TEST_AM_RAW_METRIC2")
public class RawMetric2 implements Serializable {

	private Long id = new Long(-1);
    
	@Column(nullable=false, length=100)
    private String artifact;
	
	@Column(nullable=false, length=100)
	private String host;
	
	@Column(nullable=false, length=100)
	private String instance;
	
	@Column(nullable=true, length=100)
	private String metricType;
	
	@Column(nullable=true, length=100)
	private String resource;
	
	@Column(nullable=true, length=100)
	private String source;
	
	@Column(nullable=false, length=16, precision=6)
	private double metric;
	
	@Column(nullable=false)
	private Date timeStamp;
	
	// @Column(nullable=false)
    // private String Time // TODO check sense of this item 
	
	@Column(nullable=true, length=1000)
    private String parameters; //Object parameters; // TODO check if possible use List or Array!!!
	
	@Column(nullable=true, length=1000)
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
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

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
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

	public double getMetric() {
		return metric;
	}

	public void setMetric(double metric) {
		this.metric = metric;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
    
}
