
-------------------------------------------------------------------------------------------------------------------------
-- Action classes

-- insert resources (action class) - very dynamic data
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename)
SELECT am_rsc_seq.NEXTVAL,
       (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'ACTCLS'),
       dc.class_name
FROM dm_class dc;
COMMIT;

-- insert sources (users) - very dynamic data
INSERT INTO am_source(am_src_id, am_mty_id, sourcename)
SELECT am_src_seq.NEXTVAL,
       (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'ACTCLS'),
       dt.tropics_user
FROM dm_tropics_user dt;
COMMIT;

-- insert fact table 
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
COMMIT;

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
COMMIT;

-- insert sources (no source) - stable data
INSERT INTO am_source(am_src_id, am_mty_id, sourcename, sourcecode)
VALUES(am_src_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = 'APPSLC'), 'No source', 'APPNOS');
COMMIT;

-- insert fact table 
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

-- check loaded data
SELECT COUNT(*) FROM vmd_ex_tsc_all;

SELECT COUNT(*) FROM vam_metricsdata;

SELECT vam.metricvalue, vam.ts
FROM   vam_metricsdata vam
WHERE  vam.artifactcode = 'APP'
AND    vam.hostcode = 'EXPHST'
AND    vam.instancecode = 'PETSTR'
AND    vam.metriccode = 'APPSLC'
AND    vam.resourcecode = 'LOGUSR' -- 'DBCMPL' 
;


-------------------------------------------------------------------------------------------------------------------------
-- Report metrics





