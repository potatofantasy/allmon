

-- fill up default data for not dynamic dimensions
BEGIN
  -- OS, AppMet, Rep, JVM, DB, HW
  am_allmetric_mngr.meta_add_artifact('Operating System', 'OS'); 
  am_allmetric_mngr.meta_add_artifact('Application', 'APP'); 
  am_allmetric_mngr.meta_add_artifact('Report', 'REP'); 
  am_allmetric_mngr.meta_add_artifact('Java Virtual Machine', 'JVM'); 
  am_allmetric_mngr.meta_add_artifact('Database', 'DB'); 
  am_allmetric_mngr.meta_add_artifact('Hardware', 'HW'); 
  COMMIT;
END;

BEGIN
  am_allmetric_mngr.meta_add_metrictype('APP', 'Struts Action Class', 'ACTCLS', 'ms'); 
  am_allmetric_mngr.meta_add_metrictype('APP', 'Service Level Check', 'APPSLC'); 
  am_allmetric_mngr.meta_add_metrictype('REP', 'Report Jobs Execs', 'REPEXE'); 
  COMMIT;
END;

BEGIN
  am_allmetric_mngr.meta_add_instance('APP', 'Petstore', 'PETSTR'); 
  am_allmetric_mngr.meta_add_instance('REP', 'Petstore Reports', 'PETREP'); 
  COMMIT;
END;

BEGIN
  am_allmetric_mngr.meta_add_host('Example Host', 'EXPHST', '123.123.123.123');
  COMMIT;
END;


-------------------------------------------------------------------------------------------------------------------------
-- database link to monitor
CREATE DATABASE LINK monitor
CONNECT TO monitor
IDENTIFIED BY monitor
USING 'monitor';

-------------------------------------------------------------------------------------------------------------------------
-- Action classes

-- insert resources (action class) - very dynamic data
/*
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename)
SELECT am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'ACTCLS'), dc.class_name
FROM dm_class@monitor dc;
*/
DECLARE
  TYPE t IS RECORD(d VARCHAR2(1024));
  CURSOR c(d VARCHAR2) RETURN t IS 
    SELECT dc.class_name FROM dm_class@monitor dc;
  v VARCHAR2(1024);
BEGIN
  OPEN c(1);
  LOOP
    FETCH c INTO v;
    EXIT WHEN c%NOTFOUND;
    am_allmetric_mngr.data_add_resource('ACTCLS', v); 
    --dbms_output.put_line(v);
  END LOOP;
END;
/
COMMIT;

-- insert sources (users) - very dynamic data
/*
INSERT INTO am_source(am_src_id, am_mty_id, sourcename)
SELECT am_src_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'ACTCLS'), dt.tropics_user
FROM dm_tropics_user dt;
*/
DECLARE
  TYPE t IS RECORD(d VARCHAR2(1024));
  CURSOR c(d VARCHAR2) RETURN t IS 
    SELECT dt.tropics_user FROM dm_tropics_user@monitor dt;
  v VARCHAR2(1024);
BEGIN
  OPEN c(1);
  LOOP
    FETCH c INTO v;
    EXIT WHEN c%NOTFOUND;
    am_allmetric_mngr.data_add_source('ACTCLS', v); 
    dbms_output.put_line(v);
  END LOOP;
END;
/
COMMIT;

-- insert fact table 
/*
--SELECT * FROM fc_action fa WHERE fa.datetime > SYSDATE - 1
INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
SELECT am_met_seq.NEXTVAL, 
       (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = 'PETSTR'),
       (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = 'EXPHST'),
       ar.am_rsc_id,
       s.am_src_id,
       (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(fa.datetime, 'DD')),
       (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(fa.datetime, 'HH24') AND t.minute = to_char(fa.datetime, 'MI')),
       fa.exectime_ms,
       fa.datetime 
FROM  fc_action fa,  dm_class dc, am_resource ar, dm_tropics_user dt, am_source s
WHERE fa.dm_cls_id = dc.dm_cls_id
AND   dc.class_name = ar.resourcename
AND   fa.dm_tru_id = dt.dm_tru_id
AND   dt.tropics_user = s.sourcename
AND   fa.datetime > SYSDATE - 1; --AND fa.datetime > SYSDATE - 10;
*/
CREATE TABLE fc_action AS SELECT * FROM vfc_actions_all_dims_all_msrs@monitor WHERE datetime BETWEEN SYSDATE - 15 AND SYSDATE - 6;--2/24;
DECLARE
  TYPE t IS RECORD(resourcename am_resource.resourcename%TYPE, sourcename am_source.sourcename%TYPE, datetime DATE, exectime_ms am_metricsdata.metricvalue%TYPE);
  CURSOR c RETURN t IS 
    SELECT ar.resourcename,
           s.sourcename,
           fa.datetime,
           fa.exectime_ms           
    FROM  fc_action fa, am_resource ar, am_source s
    WHERE fa.class_name = ar.resourcename
    AND   fa.tropics_user = s.sourcename;
  v t;
BEGIN
  OPEN c;
  LOOP
    FETCH c INTO v;
    EXIT WHEN c%NOTFOUND;
    am_allmetric_mngr.data_add_metricsdata('PETSTR', 'EXPHST', 'ACTCLS', 
                                           pi_resourcename => v.resourcename, pi_sourcename => v.sourcename, pi_datetime => v.datetime, pi_metricvalue => v.exectime_ms); 
    --dbms_output.put_line(v);
  END LOOP;
END;
--DROP TABLE fc_action;
--BEGIN am_allmetric_mngr.data_delete_metricsdata('PETSTR', 'EXPHST', 'ACTCLS'); END;
BEGIN am_allmetric_mngr.admin_rebuilt_indexes; END;

-- check loaded data
SELECT COUNT(*) FROM am_metricsdata;
SELECT COUNT(*) FROM vam_metricsdata;

SELECT vam.resourcename, vam.sourcename, vam.metricvalue, vam.ts
FROM   vam_metricsdata vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'ACTCLS';

SELECT vam.resourcename, COUNT(*) 
FROM   vam_metricsdata vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'ACTCLS'
GROUP BY vam.resourcename;

SELECT vamc.year, vamc.month, vamc.DAY, COUNT(*) 
FROM   vam_metricsdata_cal vamc
WHERE  vamc.artifactcode = 'APP'
AND    vamc.hostcode = 'EXPHST'
AND    vamc.instancecode = 'PETSTR'
AND    vamc.metriccode = 'ACTCLS'
GROUP BY vamc.year, vamc.month, vamc.DAY;

-------------------------------------------------------------------------------------------------------------------------
-- Service level check

-- insert resources (name of service check) - stable data
/*
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'Service Status', 'SRVSTS');
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'No of logged-in users', 'LOGUSR');
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'No of stateful EJBs', 'SFEJBS');
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'Java calculation time - Web container', 'JAVWEB');
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'Java calculation time - EJB container', 'JAVEJB');
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'Database calculation time - Simple', 'DBSMPL');
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode) 
VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'Database calculation time - Complex', 'DBCMPL');
*/
BEGIN
  am_allmetric_mngr.data_add_resource('APPSLC', 'Service Status', 'SRVSTS'); 
  am_allmetric_mngr.data_add_resource('APPSLC', 'No of logged-in users', 'LOGUSR');
  am_allmetric_mngr.data_add_resource('APPSLC', 'No of stateful EJBs', 'SFEJBS');
  am_allmetric_mngr.data_add_resource('APPSLC', 'Java calculation time - Web container', 'JAVWEB');
  am_allmetric_mngr.data_add_resource('APPSLC', 'Java calculation time - EJB container', 'JAVEJB');
  am_allmetric_mngr.data_add_resource('APPSLC', 'Database calculation time - Simple', 'DBSMPL');
  am_allmetric_mngr.data_add_resource('APPSLC', 'Database calculation time - Complex', 'DBCMPL');
  COMMIT;
END;

-- insert sources (no source) - stable data

-- insert fact table 
/*
INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
SELECT am_met_seq.NEXTVAL, 
       (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = 'PETSTR'),
       (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = 'EXPHST'),
       (SELECT ar.am_rsc_id FROM am_resource ar WHERE ar.resourcecode = 'LOGUSR'),
       (SELECT s.am_src_id FROM am_source s WHERE s.sourcecode = 'APPNOS'),
       (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(vt.datetime, 'DD')),
       (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(vt.datetime, 'HH24') AND t.minute = to_char(vt.datetime, 'MI')),
       nvl(vt.tropics_users, 0),
       vt.datetime 
FROM  vmd_ex_tsc_all vt
WHERE vt.datetime > SYSDATE - 1 -- AND vt.datetime > SYSDATE - 10
AND   vt.tsc_host = 'toras51:7779';
COMMIT;

INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
SELECT am_met_seq.NEXTVAL, 
       (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = 'PETSTR'),
       (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = 'EXPHST'),
       (SELECT ar.am_rsc_id FROM am_resource ar WHERE ar.resourcecode = 'SFEJBS'),
       (SELECT s.am_src_id FROM am_source s WHERE s.sourcecode = 'APPNOS'),
       (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(vt.datetime, 'DD')),
       (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(vt.datetime, 'HH24') AND t.minute = to_char(vt.datetime, 'MI')),
       nvl(vt.stateful_ejb_count, 0),
       vt.datetime 
FROM  vmd_ex_tsc_all vt
WHERE vt.datetime > SYSDATE - 1 -- AND vt.datetime > SYSDATE - 10
AND   vt.tsc_host = 'toras51:7779';
COMMIT;

INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
SELECT am_met_seq.NEXTVAL, 
       (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = 'PETSTR'),
       (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = 'EXPHST'),
       (SELECT ar.am_rsc_id FROM am_resource ar WHERE ar.resourcecode = 'DBCMPL'),
       (SELECT s.am_src_id FROM am_source s WHERE s.sourcecode = 'APPNOS'),
       (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(vt.datetime, 'DD')),
       (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(vt.datetime, 'HH24') AND t.minute = to_char(vt.datetime, 'MI')),
       nvl(vt.db_complex_calc_time, 0),
       vt.datetime 
FROM  vmd_ex_tsc_all vt
WHERE vt.datetime > SYSDATE - 1 -- AND vt.datetime > SYSDATE - 10
AND   vt.tsc_host = 'toras51:7779';
COMMIT;
*/

DECLARE
  TYPE t IS RECORD(datetime DATE, tropics_users am_metricsdata.metricvalue%TYPE, stateful_ejb_count am_metricsdata.metricvalue%TYPE, db_complex_calc_time am_metricsdata.metricvalue%TYPE);
  CURSOR c RETURN t IS 
    SELECT vt.datetime, nvl(vt.tropics_users, 0), nvl(vt.stateful_ejb_count, 0), nvl(vt.db_complex_calc_time, 0)
    FROM  vmd_ex_tsc_all@monitor vt
    WHERE vt.datetime < SYSDATE - 2 -- AND vt.datetime > SYSDATE - 10
    AND   vt.tsc_host = 'toras51:7779';
  v t;
BEGIN
  OPEN c;
  LOOP
    FETCH c INTO v;
    EXIT WHEN c%NOTFOUND;
    am_allmetric_mngr.data_add_metricsdata('PETSTR', 'EXPHST', 'APPSLC', 'LOGUSR', v.datetime, v.tropics_users); 
    am_allmetric_mngr.data_add_metricsdata('PETSTR', 'EXPHST', 'APPSLC', 'SFEJBS', v.datetime, v.stateful_ejb_count); 
    am_allmetric_mngr.data_add_metricsdata('PETSTR', 'EXPHST', 'APPSLC', 'DBCMPL', v.datetime, v.db_complex_calc_time); 
    --dbms_output.put_line(v.datetime || '-' || v.tropics_users);
  END LOOP;
END;

-- check loaded data
SELECT COUNT(*) FROM am_metricsdata;
SELECT COUNT(*) FROM am_metricsdata a WHERE a.am_src_id IS NULL;
SELECT COUNT(*) FROM vam_metricsdata;
SELECT COUNT(*) FROM vam_metricsdata v WHERE v.am_src_id IS NULL;

SELECT vam.metricvalue, vam.ts
FROM   vam_metricsdata vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'APPSLC'
AND    vam.resourcecode = 'LOGUSR';

SELECT vam.metricvalue, vam.ts
FROM   vam_metricsdata_cal vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'APPSLC'
AND    vam.resourcecode = 'DBCMPL'
AND    vam.year = 2009
AND    vam.MONTH = 5;

SELECT vam.resourcename, vam.metricvalue, vam.ts
FROM   vam_metricsdata vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'APPSLC'
AND    vam.resourcecode = 'LOGUSR';

SELECT vam.year, vam.month, vam.DAY, COUNT(*), AVG(vam.metricvalue)
FROM   vam_metricsdata_cal vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'APPSLC'
AND    vam.resourcecode = 'LOGUSR' -- 'DBCMPL' 
GROUP BY vam.year, vam.month, vam.DAY
ORDER BY vam.year, vam.month, vam.DAY;

SELECT * FROM am_metrictype


-------------------------------------------------------------------------------------------------------------------------
-- Report metrics

-- insert resources (action class) - very dynamic data
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename)
SELECT am_rsc_seq.NEXTVAL,
       (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'REPEXE'),
       rw.job_name
FROM   (SELECT DISTINCT rw.job_name FROM tmp_rw_server_job_queue_hist rw) rw;
COMMIT;

-- insert sources (users) - very dynamic data
-- NO SOURCE DATA

-- insert fact table 
/*
SELECT * FROM reports.rw_server_job_queue_hist rw 
WHERE rw.job_name = 'OP_TourSeriesCancellation_O'
--AND   rw.status_code != 1 
AND   rw.started > SYSDATE - 1/24

CREATE TABLE tmp_rw_server_job_queue_hist2 AS 
SELECT * FROM rw_server_job_queue_hist@torrepo rw 
WHERE ROWNUM < 1000000;
*/
-- SELECT DISTINCT rw.status_code, rw.status_message FROM tmp_rw_server_job_queue_hist rw;
-- SELECT * FROM tmp_rw_server_job_queue_hist rw;
INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
SELECT am_met_seq.NEXTVAL, 
       (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = 'PETREP'),
       (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = 'EXPHST'),
       ar.am_rsc_id,
       (SELECT sr.am_src_id FROM am_source sr WHERE sr.sourcecode = 'REPNOS'),
       (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(rw.started, 'DD')),
       (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(rw.started, 'HH24') AND t.minute = to_char(rw.started, 'MI')),
       rw.run_elapse,
       rw.started
FROM   tmp_rw_server_job_queue_hist rw, am_resource ar
WHERE  rw.job_name = ar.resourcename
AND    rw.status_code != 1 -- load all finished (also with errors)
;
COMMIT;

--DELETE FROM am_metricsdata am WHERE am.am_rsc_id IN ( SELECT ar.am_rsc_id FROM am_resource ar WHERE ar.am_mty_id = 22)

-- check loaded data
SELECT COUNT(*) FROM am_metricsdata;
SELECT COUNT(*) FROM vam_metricsdata;

SELECT * FROM am_metrictype;
select * FROM am_resource;
select * FROM am_source 
select * FROM am_metricsdata m WHERE m.am_rsc_id = 2414;

SELECT * FROM tmp_rw_server_job_queue_hist rw;

SELECT vam.resourcename, vam.metricvalue, vam.ts
FROM   vam_metricsdata vam
WHERE  vam.artifactcode = 'REP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETREP'
AND    vam.metriccode = 'REPEXE';

SELECT vam.year, vam.month, vam.DAY, count(*), avg(vam.metricvalue)
FROM   vam_metricsdata_cal vam
WHERE  vam.artifactcode = 'REP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETREP'
AND    vam.metriccode = 'REPEXE'
GROUP BY vam.year, vam.month, vam.DAY;

