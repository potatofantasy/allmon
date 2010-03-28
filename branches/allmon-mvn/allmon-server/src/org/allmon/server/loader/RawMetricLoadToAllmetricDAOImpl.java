package org.allmon.server.loader;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RawMetricLoadToAllmetricDAOImpl extends HibernateDaoSupport implements RawMetricLoadToAllmetricDAO {
    
    private static final Log log = LogFactory.getLog(RawMetricLoadToAllmetricDAOImpl.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void load() {
        RawMetricLoadToAllmetric proc = new RawMetricLoadToAllmetric(dataSource);
        proc.executeProc();
    }
}