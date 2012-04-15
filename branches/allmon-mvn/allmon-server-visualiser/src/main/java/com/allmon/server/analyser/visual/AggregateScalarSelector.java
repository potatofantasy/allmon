package com.allmon.server.analyser.visual;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class AggregateScalarSelector extends HibernateDaoSupport {

	public List<AggregateScalarData> select(String resourceName) {
		String query = 
			"select to_char(r.ts, 'YYYY-MM-DD') as day,\n" +
			"       to_char(r.ts, 'YYYY-MM-DD HH24') as hour,\n" + 
			"       to_char(r.ts, 'YYYY-MM-DD HH24:MI') as mi,\n" + 
			"       count(*) count,\n" + 
			"       sum(r.metricvalue) as sum, -- for actions\n" + 
			"       avg(r.metricvalue) as avg,\n" + 
			"       min(r.metricvalue) as min,\n" + 
			"       max(r.metricvalue) as max,\n" + 
			"       stddev(r.metricvalue) as stddev\n" + 
			"  from am_raw_metric r\n" + 
			" where r.resourcename = :resourcename\n" + 
			" group by\n" + 
			"       to_char(r.ts, 'YYYY-MM-DD'),\n" + 
			"       to_char(r.ts, 'YYYY-MM-DD HH24'),\n" + 
			"       to_char(r.ts, 'YYYY-MM-DD HH24:MI')\n" + 
			" order by mi";

		SQLQuery q = getSession().createSQLQuery(query);
//		q.addScalar("param1", Hibernate.STRING);
//		q.addScalar("param2", Hibernate.STRING);
		q.setResultTransformer(Transformers.aliasToBean(AggregateScalarData.class));
		q.setParameter("resourcename", resourceName);
		List<AggregateScalarData> list = q.list();
		for (AggregateScalarData object : list) {
			System.out.println(object);
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
		}
		return list;
	}
	
    private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
    		new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });
    
	public static void main(String[] args) {
		
		AggregateScalarSelector scalarSelector = (AggregateScalarSelector)appContext.getBean("scalarSelector");
		
		scalarSelector.select("Mem ActualUsed:");
	}
	
}

