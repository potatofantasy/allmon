package com.allmon.server.analyser.visual;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Entity;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

//@NamedQueries( {
//		@NamedQuery(name = "planeType.findById", 
//				query = "select p from PlaneType p left join fetch p.modelPlanes where id=:id"),
//		@NamedQuery(name = "planeType.findAll", 
//				query = "select p from PlaneType p"),
//		@NamedQuery(name = "planeType.delete", 
//				query = "delete from PlaneType where id=:id") })

//@NamedNativeQueries({
//	@NamedNativeQuery(
//		name = "findStockByStockCodeNativeSQL",
//		query = 
//			"SELECT sel.segment_name || ' [' || sel.segment_type || ']' as segment_name, " +
//			"		sel.tablespace_name, sel.mb, sel.num_rows, sel.actual_num_rows, sel.Avg_Row_Len,\n" +
//			"       CASE WHEN actual_num_rows > 0 THEN bytes / actual_num_rows ELSE NULL END AS bytes_per_row\n" + 
//			"FROM (SELECT us.segment_name, us.segment_type, us.bytes, us.bytes/1024/1024 AS mb, us.blocks,\n" + 
//			"             tab.tablespace_name, tab.status, tab.num_rows, am_allmetric_mngr.get_number_of_rows(tab.table_name) AS actual_num_rows, tab.Avg_Row_Len, tab.last_analyzed\n" + 
//			"      FROM   user_segments us, user_tables tab --, user_indexes ind\n" + 
//			"      WHERE  (us.segment_name LIKE 'AM_%' OR us.segment_name LIKE 'VMAM_%')\n" + 
//			"      AND    us.segment_name = tab.table_name(+)) sel\n" + 
//			"ORDER BY mb DESC, 1",
//        resultClass = DBStatData.class
//	)
//})
@Entity
//@Table //(name = "stock")
public class DBStatData {

	public String SEGMENT_NAME;
	public String TABLESPACE_NAME;
	public BigDecimal MB;
	public BigDecimal NUM_ROWS;
	public BigDecimal ACTUAL_NUM_ROWS; 
	public BigDecimal AVG_ROW_LEN;
	public BigDecimal BYTES_PER_ROW;
	
	
}
