package org.allmon.loader.loadtest;

import org.allmon.loader.RawMetric;
import org.allmon.server.receiver.LoadRawMetric;

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
public class LoadRawMetricTest extends LoadTestedClass {
    
    public static void main(String[] args) {
        try {
            LoadRawMetricTest loadRawMetric = new LoadRawMetricTest(1, 1);
            loadRawMetric.runConcurently();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LoadRawMetricTest(int threadNum, long startingTime) {
        super(threadNum, startingTime);
        //appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
        //System.out.println("Classpath loaded");
    }

    public void runConcurently() {
        System.out.println("LoadRawMetric started");
        long t0 = System.nanoTime();
        
        LoadRawMetric loadRawMetric = new LoadRawMetric(); //new LoadRawMetric(threadNum, startingTime);
        long t1 = System.nanoTime();
        
        for (int i = 0; i < 2000; i++) {
            RawMetric metric = new RawMetric();
            String str = ">" + Math.random() + ">" + Math.random() + ">" + Math.random() + ">" + Math.random();
            metric.setMetric(str);
            loadRawMetric.storeMetric(metric);
        }
        
        long t2 = System.nanoTime();
        
        System.out.println("LoadRawMetric initialized in " + (t1 - t0)/1000000);
        System.out.println("LoadRawMetric metrics loaded in " + (t2 - t1)/1000000);
                    
        System.out.println("LoadRawMetric end");
    }

}
