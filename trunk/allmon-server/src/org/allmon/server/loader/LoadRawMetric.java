package org.allmon.server.loader;

import org.allmon.common.MetricMessageWrapper;
import org.allmon.server.receiver.MetricMessageConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Class is loading received from allmon-client side metrics and load them to database in "raw" form. 
 */
public class LoadRawMetric {

    private static final Log logger = LogFactory.getLog(LoadRawMetric.class);
    
    private static final ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/server/loader/spring-hibernate.xml" });
    //private ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "org/allmon/loader/spring-hibernate.xml" });
    
    // TODO to improve performance:
    // TODO the loading process should be split in to many concurrently running threads
    // TODO use batch loading!
    public void storeMetric(MetricMessageWrapper metricMessageWrapper) {
        RawMetric2DAOImpl rawMetric2DAOImpl = (RawMetric2DAOImpl)appContext.getBean("rawMetric2DAOTarget");
        
        // convert metricMessageWrapper and load metrics data to the database
        MetricMessageConverter messageConverter = new MetricMessageConverter();
        RawMetric2[] rawMetricTab = messageConverter.convert(metricMessageWrapper);
//        for (int i = 0; i < rawMetricTab.length; i++) {
//            try {
//                rawMetric2DAOImpl.addMetric(rawMetricTab[i]);
//            } catch (Exception ex) {
//                logger.error(ex.getMessage(), ex);
//            }
//		  }
        // Runs loading process of all metrics data in the table in many threads
        RawMetricConcurrentLoader concurrentLoader = new RawMetricConcurrentLoader(
                rawMetric2DAOImpl, rawMetricTab, 10); // TODO add external parameter
        try {
            concurrentLoader.runLoad();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
                
        logger.debug(">>>>>>>>>>>>>>>> Metrics stored: " + metricMessageWrapper.toString());
    }
    
    public void loadAllmetric() {
        long t0 = System.currentTimeMillis();
        RawMetricLoadToAllmetricDAOImpl loadToAllmetricDAOImpl = 
            (RawMetricLoadToAllmetricDAOImpl)appContext.getBean("rawMetricLoadToAllmetricDAOTarget");
        loadToAllmetricDAOImpl.load();
        logger.debug(">>>>>>>>>>>>>>>> Metrics stored in allmetric schema in " + (System.currentTimeMillis() - t0) + "ms");
    }
    
}
