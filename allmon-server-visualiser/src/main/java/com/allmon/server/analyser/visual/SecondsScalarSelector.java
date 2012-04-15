package com.allmon.server.analyser.visual;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SecondsScalarSelector extends HibernateDaoSupport {

	public List<SecondsScalarData> select(String resourceName, Date dateFrom, Date dateTo, boolean firstDerivative) {
		String query = 
			"select to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as sec,\n" +
			"       count(*) count,\n" + 
			"       sum(r.metricvalue) as sum, -- for actions\n" + 
			"       avg(r.metricvalue) as avg,\n" + 
			"       min(r.metricvalue) as min,\n" + 
			"       max(r.metricvalue) as max\n" + 
			"  from am_raw_metric r\n" + 
			" where r.resourcename = :resourcename\n" + 
			"  and  r.ts >= to_date(:datefrom, 'YYYY-MM-DD HH24:MI:SS')\n" + 
			"  and  r.ts <= to_date(:dateto, 'YYYY-MM-DD HH24:MI:SS')\n" + 
			" group by to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')\n" + 
			" order by sec";

		SQLQuery q = getSession().createSQLQuery(query);
//		q.addScalar("param1", Hibernate.STRING);
//		q.addScalar("param2", Hibernate.STRING);
		q.setResultTransformer(Transformers.aliasToBean(SecondsScalarData.class));
		q.setParameter("resourcename", resourceName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // DB: 'YYYY-MM-DD HH24:MI:SS'
		q.setParameter("datefrom", sdf.format(dateFrom)); //'2011-11-16 14:00:00'
		q.setParameter("dateto", sdf.format(dateTo));
		List<SecondsScalarData> list = q.list();
		
		if (!firstDerivative) {
			for (SecondsScalarData scalar : list) {
				System.out.println(scalar);
			}
			return list;
		} else {
			List<SecondsScalarData> dlist = new ArrayList<SecondsScalarData>();
			for (int i = 0; i < list.size() - 1; i++) {
				SecondsScalarData scalara = list.get(i);
				SecondsScalarData scalarb = list.get(i + 1);
				SecondsScalarData dscalar = scalarb.subtract(scalara);
				System.out.println(dscalar);
				dlist.add(dscalar);
			}
			return dlist;
		}
	}
	
    private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
    		new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });
    
	public static void main(String[] args) throws ParseException {
		
		SecondsScalarSelector scalarSelector = (SecondsScalarSelector)appContext.getBean("secondsScalarSelector");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		scalarSelector.select("Mem ActualUsed:", 
				sdf.parse("2011-11-16 14:00:00"), sdf.parse("2011-11-16 15:00:00"), false);
		
		scalarSelector.select("DiskReadBytes:", 
				sdf.parse("2011-11-16 14:00:00"), sdf.parse("2011-11-16 15:00:00"), true);
		
		
	}
	
}

