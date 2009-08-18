package org.allmon.server.receiver;

import org.allmon.loader.RawMetric;
import org.allmon.loader.RawMetricDAOImpl;
import org.allmon.loader.loadtest.LoadTestedClass;
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
public class LoadRawMetric extends LoadTestedClass {

    private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
    //private ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });

    public static void main(String[] args) {
        try {
            LoadRawMetric loadRawMetric = new LoadRawMetric(1, 1);
            loadRawMetric.runConcurently();
            loadRawMetric.runConcurently();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LoadRawMetric() {
        super(1, 1);
    }
    
    public void runConcurently() {
        System.out.println("LoadRawMetric started");
        long t0 = System.nanoTime();
        
        LoadRawMetric loadRawMetric = new LoadRawMetric(threadNum, startingTime);
        long t1 = System.nanoTime();
        
        loadRawMetric.storeMetrics();
        long t2 = System.nanoTime();
        
        System.out.println("LoadRawMetric initialized in " + (t1 - t0)/1000000);
        System.out.println("LoadRawMetric metrics loaded in " + (t2 - t1)/1000000);
                    
        System.out.println("LoadRawMetric end");
    }

    public LoadRawMetric(int threadNum, long startingTime) {
        super(threadNum, startingTime);
        //appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
        //System.out.println("Classpath loaded");
    }

    private void storeMetrics() {
        RawMetricDAOImpl rawMetricDAOImpl = (RawMetricDAOImpl) appContext.getBean("rawMetricDAOTarget");
        for (int i = 0; i < 2000; i++) {
            RawMetric metric = new RawMetric();
            metric.setMetric(">" + Math.random() + ">" + Math.random() + ">" + Math.random() + ">" + Math.random());
            rawMetricDAOImpl.addMetric(metric);
        }
    }
    
    public void storeMetric(String metricString) {
        RawMetricDAOImpl rawMetricDAOImpl = (RawMetricDAOImpl) appContext.getBean("rawMetricDAOTarget");
        RawMetric metric = new RawMetric();
        metric.setMetric(metricString);
        rawMetricDAOImpl.addMetric(metric);
        System.out.println(">>>>>>>>>>>>>>>> Metric stored: " + metricString);
    }

}
