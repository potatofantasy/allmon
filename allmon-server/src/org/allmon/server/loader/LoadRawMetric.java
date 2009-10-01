package org.allmon.server.loader;

import org.allmon.common.MetricMessageWrapper;
import org.allmon.server.receiver.MetricMessageConverter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 */
public class LoadRawMetric {

    private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/server/loader/spring-hibernate.xml" });
    //private ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
    
    /**
     * @deprecated TODO delete this method!
     */
    public void storeMetric(String metricString) {
        RawMetricDAOImpl rawMetricDAOImpl = (RawMetricDAOImpl)appContext.getBean("rawMetricDAOTarget");
        RawMetric metric = new RawMetric();
        metric.setMetric(metricString);
        rawMetricDAOImpl.addMetric(metric);
        System.out.println(">>>>>>>>>>>>>>>> Metric stored: " + metricString);
    }

    /**
     * @deprecated TODO delete this method - it is not used
     */
    public void storeMetric(RawMetric rawMetric) {
        RawMetricDAOImpl rawMetricDAOImpl = (RawMetricDAOImpl)appContext.getBean("rawMetricDAOTarget");
        rawMetricDAOImpl.addMetric(rawMetric);
        System.out.println(">>>>>>>>>>>>>>>> Metric stored: " + rawMetric.toString());
    }
    
    public void storeMetric(MetricMessageWrapper metricMessageWrapper) {
        RawMetric2DAOImpl rawMetric2DAOImpl = (RawMetric2DAOImpl)appContext.getBean("rawMetric2DAOTarget");
        
        // convert metricMessageWrapper and load metrics data to the database
        MetricMessageConverter messageConverter = new MetricMessageConverter();
        RawMetric2[] rawMetricTab = messageConverter.convert(metricMessageWrapper);
        for (int i = 0; i < rawMetricTab.length; i++) {
        	rawMetric2DAOImpl.addMetric(rawMetricTab[i]);
		}
        
        System.out.println(">>>>>>>>>>>>>>>> Metric stored: " + metricMessageWrapper.toString());
    }
    
}
