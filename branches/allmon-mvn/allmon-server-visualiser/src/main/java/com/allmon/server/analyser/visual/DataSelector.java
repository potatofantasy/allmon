package com.allmon.server.analyser.visual;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DataSelector extends HibernateDaoSupport {
	
	public void select() {
		//getHibernateTemplate().getSessionFactory()
		String query = 
			"SELECT sel.segment_name || ' [' || sel.segment_type || ']' as segment_name, " +
			"		sel.tablespace_name, sel.mb, sel.num_rows, sel.actual_num_rows, sel.Avg_Row_Len,\n" +
			"       CASE WHEN actual_num_rows > 0 THEN bytes / actual_num_rows ELSE NULL END AS bytes_per_row\n" + 
			"FROM (SELECT us.segment_name, us.segment_type, us.bytes, us.bytes/1024/1024 AS mb, us.blocks,\n" + 
			"             tab.tablespace_name, tab.status, tab.num_rows, am_allmetric_mngr.get_number_of_rows(tab.table_name) AS actual_num_rows, tab.Avg_Row_Len, tab.last_analyzed\n" + 
			"      FROM   user_segments us, user_tables tab --, user_indexes ind\n" + 
			"      WHERE  (us.segment_name LIKE 'AM_%' OR us.segment_name LIKE 'VMAM_%')\n" + 
			"      AND    us.segment_name = tab.table_name(+)) sel\n" + 
			"ORDER BY mb DESC, 1";

		SQLQuery q = getSession().createSQLQuery(query);
//		q.addScalar("param1", Hibernate.STRING);
//		q.addScalar("param2", Hibernate.STRING);
		q.setResultTransformer(Transformers.aliasToBean(DBStatData.class));
//		q.setParameter("queryParam1", "some value");
		List list = q.list();
		for (Object object : list) {
			System.out.println(object);
		}
	}
	
	public void select2() {
		Query query = 
			getSession().getNamedQuery("findStockByStockCodeNativeSQL").
				setString("stockCode", "7277");
	}
	
	
    private static final ApplicationContext appContext = new ClassPathXmlApplicationContext(
    		new String[] { "classpath:META-INF/allmonReceiverAppContext-hibernate.xml" });
    
	public static void main(String[] args) {
		
		DataSelector dataSelector = (DataSelector)appContext.getBean("dataSelector");
		
		dataSelector.select();
//		dataSelector.select2();
	}
	
}

