package org.allmon.server.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Run loading process of all metrics data in the table in many threads.
 * 
 * TODO prevent creating too many threads as it is configured
 * TODO do not create many threads for small amounts of data to load
 * 
 */
public class RawMetricConcurrentLoader extends AbstractConcurrentLoader<RawMetric2> {

    private static final Log logger = LogFactory.getLog(RawMetricConcurrentLoader.class);
    
    private final RawMetric2DAO rawMetric2DAO;
    
    public RawMetricConcurrentLoader(RawMetric2DAO rawMetric2DAO, RawMetric2[] loadingObjects, int threadsCount) {
        super(loadingObjects, threadsCount);
        this.rawMetric2DAO = rawMetric2DAO;
    }

    void loadCall(RawMetric2 loadingObject, int thread, int iteration) throws Exception {
        rawMetric2DAO.addMetric(loadingObject);
    }

}
