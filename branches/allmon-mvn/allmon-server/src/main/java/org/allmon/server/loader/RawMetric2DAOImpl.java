package org.allmon.server.loader;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RawMetric2DAOImpl extends HibernateDaoSupport implements RawMetric2DAO {

    private int maxConcurrentThreadsCount = 1;
    
    private int preferedMetricsCountPerThread = 10;
    
    public void addMetric(RawMetric2 metric) {
        getHibernateTemplate().save(metric);
    }

    public int getMaxConcurrentThreadsCount() {
        return maxConcurrentThreadsCount;
    }

    public void setMaxConcurrentThreadsCount(int maxConcurrentThreadsCount) {
        this.maxConcurrentThreadsCount = maxConcurrentThreadsCount;
    }

    public int getPreferedMetricsCountPerThread() {
        return preferedMetricsCountPerThread;
    }

    public void setPreferedMetricsCountPerThread(int preferedMetricsCountPerThread) {
        this.preferedMetricsCountPerThread = preferedMetricsCountPerThread;
    }
    
    /**
     * 
     * <li> For not big amount of data to load method will return smaller than maximum 
     * count of concurrent threads, according to formula: <i>metricsCount / preferedMetricsCountPerThread + 1</i>.
     * <li> If metrics count is smaller than maximum count of concurrent threads then 
     * loading process will be executed in a separate but single thread only.
     * <li> In other cases result will be equal to maximum count of concurrent threads.
     * 
     * @param metricsCount
     * @return count of threads which should run loading process for set metricCount parameter
     */
    public int getConcurrentThreadsCount(int metricsCount) {
        if (metricsCount < maxConcurrentThreadsCount) {
            return 1;
        } else if (metricsCount / preferedMetricsCountPerThread < maxConcurrentThreadsCount) {
            return metricsCount / preferedMetricsCountPerThread + 1;
        }
        return maxConcurrentThreadsCount;
    }

}