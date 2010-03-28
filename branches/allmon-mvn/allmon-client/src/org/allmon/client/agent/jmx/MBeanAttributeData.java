package org.allmon.client.agent.jmx;

import javax.management.openmbean.CompositeDataSupport;

public class MBeanAttributeData {
    
    private long jvmId;
    private String jvmName;
    private String domainName;
    private String mbeanClass;
    private String mbeanName;
    private String mbeanAttributeName;
    private double value = 0;
    
    MBeanAttributeData(long jvmId, String jvmName, String domainName, String mbeanClass, String mbeanName, String mbeanAttributeName) {
        this.jvmId = jvmId;
        this.jvmName = jvmName;
        this.domainName = domainName;
        this.mbeanClass = mbeanClass;
        this.mbeanName = mbeanName;
        this.mbeanAttributeName = mbeanAttributeName;
    }
    
    public String toString() {
        return mbeanClass + ":" + mbeanName + ":" + mbeanAttributeName;
    }
    
    void setNumberValue(Object attribute) {
        if (attribute instanceof Number) {
            setNumberValue((Number)attribute);
        } else if (attribute instanceof Boolean) {
            setNumberValue((Boolean)attribute);
        } else if (attribute instanceof CompositeDataSupport) {
            // composite should be called
        }
    }
    
    void setNumberValue(Number attribute) {
//            logger.debug("   > " + mbeanAttributeName + " : " + attribute);
        value = Double.parseDouble(attribute.toString());
    }
    
    void setNumberValue(Boolean attribute) {
//            logger.debug("   > " + mbeanAttributeName + " : " + attribute);
        value = "true".equals(attribute.toString())?1:0;
    }

    public long getJvmId() {
        return jvmId;
    }

    public String getJvmName() {
        return jvmName;
    }
    
    public String getDomainName() {
        return domainName;
    }

    public String getMbeanName() {
        return mbeanName;
    }

    public String getMbeanAttributeName() {
        return mbeanAttributeName;
    }

    public double getValue() {
        return value;
    }
        
}


