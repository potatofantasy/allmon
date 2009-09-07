package org.allmon.server.receiver;

import org.allmon.loader.RawMetric;
import org.allmon.loader.RawMetricDAOImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
commons-logging-1.1.1.jar
commons-dbcp-1.2.2.jar
commons-pool-1.3.jar
spring-2.5.6.jar
spring-hibernate-1.2.8.jar
hibernate-2.1.8.jar
geronimo-jta_1.1_spec-1.1.jar
dom4j-1.5.jar
commons-collections-3.2.jar
ojb-1.0.rc4.jar
ehcache-1.5.0-osgi.jar
cglib-full-2.0.1.jar
ojdbc14.jar

 * @author tomasz.sikora
 *
 */
public class LoadRawMetric {

    private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
    //private ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
    
    /**
     * @deprecated TODO delete this method!
     */
    public void storeMetric(String metricString) {
        RawMetricDAOImpl rawMetricDAOImpl = (RawMetricDAOImpl) appContext.getBean("rawMetricDAOTarget");
        RawMetric metric = new RawMetric();
        metric.setMetric(metricString);
        rawMetricDAOImpl.addMetric(metric);
        System.out.println(">>>>>>>>>>>>>>>> Metric stored: " + metricString);
    }

    public void storeMetric(RawMetric metric) {
        RawMetricDAOImpl rawMetricDAOImpl = (RawMetricDAOImpl) appContext.getBean("rawMetricDAOTarget");
        rawMetricDAOImpl.addMetric(metric);
        System.out.println(">>>>>>>>>>>>>>>> Metric stored: " + metric.toString());
    }
    
}
