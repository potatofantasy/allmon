package org.allmon.server.loader;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RawMetricDAOImpl extends HibernateDaoSupport implements RawMetricDAO {

    public void addMetric(RawMetric metric) {
        getHibernateTemplate().save(metric);
    }

}
