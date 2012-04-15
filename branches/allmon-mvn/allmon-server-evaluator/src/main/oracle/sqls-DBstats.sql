
select count(*) from am_raw_metric;

-------------------------------------------------------------------------------------------------------------------------
-- administration queries - in the future can be a part of allmon aministration console

-- check data allocated segments space

SELECT sel.*,
       CASE WHEN actual_num_rows > 0 THEN bytes / actual_num_rows ELSE NULL END AS bytes_per_row 
FROM (SELECT us.segment_name, us.segment_type, us.bytes, us.bytes/1024/1024 AS mb, us.blocks, 
             tab.tablespace_name, tab.status, tab.num_rows, am_allmetric_mngr.get_number_of_rows(tab.table_name) AS actual_num_rows, tab.Avg_Row_Len, tab.last_analyzed
      FROM   user_segments us, user_tables tab --, user_indexes ind
      WHERE  (us.segment_name LIKE 'AM_%' OR us.segment_name LIKE 'VMAM_%')
      AND    us.segment_name = tab.table_name(+)) sel
ORDER BY mb DESC, 1;

SELECT sel.segment_name || ' [' || sel.segment_type || ']', sel.tablespace_name, sel.mb, sel.num_rows, sel.actual_num_rows, sel.Avg_Row_Len,
       CASE WHEN actual_num_rows > 0 THEN bytes / actual_num_rows ELSE NULL END AS bytes_per_row 
FROM (SELECT us.segment_name, us.segment_type, us.bytes, us.bytes/1024/1024 AS mb, us.blocks, 
             tab.tablespace_name, tab.status, tab.num_rows, am_allmetric_mngr.get_number_of_rows(tab.table_name) AS actual_num_rows, tab.Avg_Row_Len, tab.last_analyzed
      FROM   user_segments us, user_tables tab --, user_indexes ind
      WHERE  (us.segment_name LIKE 'AM_%' OR us.segment_name LIKE 'VMAM_%')
      AND    us.segment_name = tab.table_name(+)) sel
ORDER BY mb DESC, 1;

SELECT us.segment_type, SUM(us.bytes), SUM(us.bytes/1024/1024) AS mb, SUM(am_allmetric_mngr.get_number_of_rows(tab.table_name)) AS num_rows
FROM   user_segments us, user_tables tab
WHERE  (us.segment_name LIKE 'AM_%' OR us.segment_name LIKE 'VMAM_%')
AND    us.segment_name = tab.table_name(+)
GROUP BY us.segment_type
ORDER BY mb DESC, 1;

-- check metrics row count per day
SELECT vc.YEAR, vc.MONTH, vc.DAY, COUNT(*)
FROM   vam_metricsdata_cal vc
GROUP  BY vc.YEAR, vc.MONTH, vc.DAY
ORDER  BY 1, 2, 3;

-- data consistency checks 
SELECT 'am_metricsdata', COUNT(*) FROM am_metricsdata
UNION ALL
SELECT 'vam_metricsdata_cal', COUNT(*) FROM vam_metricsdata_cal;


