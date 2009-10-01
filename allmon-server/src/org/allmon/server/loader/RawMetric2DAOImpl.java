package org.allmon.server.loader;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RawMetric2DAOImpl extends HibernateDaoSupport implements RawMetric2DAO {

    public void addMetric(RawMetric2 metric) {
        getHibernateTemplate().save(metric);
    }

}