
-- migrate action classes values from static to dynamic schema
-- version with no hierarchic dimenstions!
--------------------------------------
--DELETE FROM fc_dimensions;
--INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date - Year,Mon,Day', 'DATE');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date - Year', 'YEAR');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date - Month', 'MONTH');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date - Day', 'DAY');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Time - Hour', 'HOUR');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Time - Minute', 'MINUTE');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Time - Second', 'SECOND');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Action Class', 'ACTCLASS');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'System user', 'SYSUSER');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Web session', 'WEBSESS');
        
INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'Execution time', 'EXECTIME');
COMMIT;

-- fill up static dimensions
--DELETE FROM fc_dimvalues dv WHERE dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MONTH');
BEGIN
  FOR yearno IN 2008..2010 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'YEAR'), yearno);
  END LOOP;
  FOR monthno IN 1..12 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MONTH'), monthno);
  END LOOP;
  FOR dayno IN 1..31 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DAY'), dayno);
  END LOOP;
  FOR hourno IN 0..23 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'HOUR'), hourno);
  END LOOP;
  FOR minno IN 0..63 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MINUTE'), minno);
  END LOOP;
  FOR secno IN 0..63 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SECOND'), secno);
  END LOOP;
END;
COMMIT;   

--------------------------------------
-- fill up dynamic dimensions
-- action classes
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) 
  SELECT fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), class_name
  FROM (
    SELECT DISTINCT ra.class_name AS class_name
    FROM   vfc_actions_all_dims_all_msrs ra 
    LEFT   OUTER JOIN fc_dimvalues dv ON (ra.class_name = dv.val)
    LEFT   OUTER JOIN fc_dimensions dim ON (dv.fc_dim_id = dim.fc_dim_id AND dim.code = 'ACTCLASS')
    WHERE  dv.fc_div_id IS NULL
    AND    ra.class_name IS NOT NULL);

-- tropics users
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) 
  SELECT fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SYSUSER'), tropics_user
  FROM (
    SELECT DISTINCT ra.tropics_user AS tropics_user
    FROM   rt_action ra 
    LEFT   OUTER JOIN fc_dimvalues dv ON (ra.tropics_user = dv.val)
    LEFT   OUTER JOIN fc_dimensions dim ON (dv.fc_dim_id = dim.fc_dim_id AND dim.code = 'SYSUSER')
    WHERE  dv.fc_div_id IS NULL
    AND    ra.tropics_user IS NOT NULL);

-- web sessions
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) 
  SELECT fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'WEBSESS'), web_session_id
  FROM (
    SELECT DISTINCT ra.web_session_id AS web_session_id
    FROM   rt_action ra 
    LEFT   OUTER JOIN fc_dimvalues dv ON (ra.web_session_id = dv.val)
    LEFT   OUTER JOIN fc_dimensions dim ON (dv.fc_dim_id = dim.fc_dim_id AND dim.code = 'WEBSESS')
    WHERE  dv.fc_div_id IS NULL
    AND    ra.web_session_id IS NOT NULL);

select * FROM fc_dimvalues

--------------------------------------
-- migrate fact data 

-- action class
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    ra.class_name = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS');

-- system user
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    ra.tropics_user = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SYSUSER');

-- web session
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    ra.web_session_id = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'WEBSESS');

-- date - year
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    to_number(to_char(ra.datetime, 'YYYY')) = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'YEAR');

-- date - month
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    to_number(to_char(ra.datetime, 'MM')) = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MONTH');

-- date - day
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    to_number(to_char(ra.datetime, 'DD')) = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DAY');

-- time - hour
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    to_number(to_char(ra.datetime, 'HH24')) = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'HOUR');

-- date - minute
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    to_number(to_char(ra.datetime, 'MI')) = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MINUTE');

-- date - second
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) 
  SELECT fc_vld_seq.NEXTVAL,
         dv.fc_div_id,
         --dv.val,
         ra.rt_action_grp_id         
  FROM   rt_action ra, 
         fc_dimvalues dv
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    to_number(to_char(ra.datetime, 'SS')) = dv.val
  AND    dv.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SECOND');

-- measure  
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) 
  SELECT fc_vlm_seq.NEXTVAL,
         (SELECT fc_msr_id FROM fc_measures WHERE code = 'EXECTIME'),
         ra.rt_action_grp_id,         
         ra.executiontime_ms
  FROM   rt_action ra
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD';
  
  
/*  
  SELECT *
  FROM   rt_action ra, 
         fc_dimvalues dv_class, fc_dimvalues dv_tropics_user, fc_dimvalues dv_web_session--, 
         --fc_dimvalues dv_year, fc_dimvalues dv_month, fc_dimvalues dv_day,
         --fc_dimvalues dv_hour, fc_dimvalues dv_minute, fc_dimvalues dv_second
  WHERE  ra.log_type = 'EXIT'
  AND    ra.environment = 'PROD'
  AND    ra.class_name = dv_class.val
  AND    ra.tropics_user = dv_tropics_user.val
  AND    ra.web_session_id = dv_web_session.val
  --
  --AND    to_date(to_char(ra.datetime, 'YYYY-MM-DD HH24:MI'), 'YYYY-MM-DD HH24:MI') = dtm.datetime
  AND    to_char(ra.datetime, 'YYYY') = dv_year.val
  AND    to_char(ra.datetime, 'MM') = dv_month.val
  AND    to_char(ra.datetime, 'DD') = dv_day.val
  AND    to_char(ra.datetime, 'HH24') = dv_hour.val
  AND    to_char(ra.datetime, 'MI') = dv_minute.val
  AND    to_char(ra.datetime, 'SS') = dv_second.val
  --
  AND    dv_class.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS')
  AND    dv_tropics_user.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SYSUSER')
  AND    dv_web_session.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'WEBSESS')
  AND    dv_year.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'YEAR')
  AND    dv_month.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MONTH')
  AND    dv_day.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DAY')
  AND    dv_hour.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'HOUR')
  AND    dv_minute.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'MINUTE')
  AND    dv_second.fc_dim_id = (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SECOND')
*/  

  
SELECT COUNT(*) FROM fc_valuesdim;
SELECT COUNT(*) FROM fc_valuesmsr;
SELECT COUNT(DISTINCT vm.rownumber) FROM fc_valuesmsr vm;



SELECT d6, COUNT(*), SUM(m1), AVG(m1)
FROM (
    SELECT sel_dim_1.rownumber, 
           sel_dim_1.val AS d1, 
           sel_dim_2.val AS d2, 
           sel_dim_3.val AS d3, 
           sel_dim_4.val AS d4, 
           sel_dim_5.val AS d5, 
           sel_dim_6.val AS d6, 
           sel_dim_7.val AS d7, 
           sel_dim_8.val AS d8, 
           sel_dim_9.val AS d9, 
           sel_msr_1.val AS m1
    FROM 
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'ACTCLASS') sel_dim_1,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'SYSUSER') sel_dim_2,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'WEBSESS') sel_dim_3,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'YEAR') sel_dim_4,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'MONTH') sel_dim_5,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'DAY') sel_dim_6,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'HOUR') sel_dim_7,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'MINUTE') sel_dim_8,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'SECOND') sel_dim_9,
    (SELECT vm.val, vm.rownumber FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECTIME') sel_msr_1
    WHERE sel_dim_1.rownumber = sel_dim_2.rownumber
    AND   sel_dim_1.rownumber = sel_dim_3.rownumber
    AND   sel_dim_1.rownumber = sel_dim_4.rownumber
    AND   sel_dim_1.rownumber = sel_dim_5.rownumber
    AND   sel_dim_1.rownumber = sel_dim_6.rownumber
    AND   sel_dim_1.rownumber = sel_dim_7.rownumber
    AND   sel_dim_1.rownumber = sel_dim_8.rownumber
    AND   sel_dim_1.rownumber = sel_dim_9.rownumber
    AND   sel_dim_1.rownumber = sel_msr_1.rownumber
    -- additional 'redundant' connections between all dimensions and measures rownumbers are helping optimize dbms queries 
    AND   sel_dim_2.rownumber = sel_dim_3.rownumber
    AND   sel_dim_2.rownumber = sel_dim_4.rownumber
    AND   sel_dim_2.rownumber = sel_dim_5.rownumber
    AND   sel_dim_2.rownumber = sel_dim_6.rownumber
    AND   sel_dim_2.rownumber = sel_dim_7.rownumber
    AND   sel_dim_2.rownumber = sel_dim_8.rownumber
    AND   sel_dim_2.rownumber = sel_dim_9.rownumber
    AND   sel_dim_2.rownumber = sel_msr_1.rownumber
    -- 
    AND   sel_dim_3.rownumber = sel_dim_4.rownumber
    AND   sel_dim_3.rownumber = sel_dim_5.rownumber
    AND   sel_dim_3.rownumber = sel_dim_6.rownumber
    AND   sel_dim_3.rownumber = sel_dim_7.rownumber
    AND   sel_dim_3.rownumber = sel_dim_8.rownumber
    AND   sel_dim_3.rownumber = sel_dim_9.rownumber
    AND   sel_dim_3.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_4.rownumber = sel_dim_5.rownumber
    AND   sel_dim_4.rownumber = sel_dim_6.rownumber
    AND   sel_dim_4.rownumber = sel_dim_7.rownumber
    AND   sel_dim_4.rownumber = sel_dim_8.rownumber
    AND   sel_dim_4.rownumber = sel_dim_9.rownumber
    AND   sel_dim_4.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_5.rownumber = sel_dim_6.rownumber
    AND   sel_dim_5.rownumber = sel_dim_7.rownumber
    AND   sel_dim_5.rownumber = sel_dim_8.rownumber
    AND   sel_dim_5.rownumber = sel_dim_9.rownumber
    AND   sel_dim_5.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_6.rownumber = sel_dim_7.rownumber
    AND   sel_dim_6.rownumber = sel_dim_8.rownumber
    AND   sel_dim_6.rownumber = sel_dim_9.rownumber
    AND   sel_dim_6.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_7.rownumber = sel_dim_8.rownumber
    AND   sel_dim_7.rownumber = sel_dim_9.rownumber
    AND   sel_dim_7.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_8.rownumber = sel_dim_9.rownumber
    AND   sel_dim_8.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_9.rownumber = sel_msr_1.rownumber
    -- filters
    AND   sel_dim_7.val = '7' -- hour
    --AND   sel_dim_2.val = '2'
    --AND   sel_dim_2.val = 'CLASS_1'
    --AND   sel_dim_3.val = 'USER_1'
    --AND   sel_msr_1.val < 1000
) GROUP BY d6;


