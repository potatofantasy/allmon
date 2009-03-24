
--------------------------------------
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date - Days', 'DAY');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Time - Hour', 'HOUR');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Action Class', 'ACTCLASS');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'System user', 'SYSUSER');
        
INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'Execution time', 'EXECTIME');
--INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'Execution time2', 'EXECCLNT');
COMMIT;

DELETE FROM fc_dimvalues;
BEGIN
  FOR dayno IN 1..31 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DAY'), dayno);
  END LOOP;
  FOR hourno IN 0..23 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'HOUR'), hourno);
  END LOOP;
END;
BEGIN
  FOR classno IN 1..2000 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_'||classno);
  END LOOP;
END;
BEGIN
  FOR userno IN 1..1000 LOOP
    INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SYSUSER'), 'USER_'||userno);
  END LOOP;
END;
COMMIT;   

--------------------------------------
DELETE FROM fc_valuesdim;
DELETE FROM fc_valuesmsr;
COMMIT;
------ 
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'DAY')) 
       + (SELECT MIN(dv.fc_div_id) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'DAY'),
       p.x + 1
FROM pivot p
WHERE p.x < 2000000;
COMMIT;
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'HOUR')) 
       + (SELECT MIN(dv.fc_div_id) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'HOUR'),
       p.x + 1
FROM pivot p
WHERE p.x < 2000000;
COMMIT;
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber)
SELECT fc_vld_seq.NEXTVAL,
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'ACTCLASS')) 
       + (SELECT MIN(dv.fc_div_id) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'ACTCLASS'),
       p.x + 1
FROM pivot p
WHERE p.x < 2000000;
COMMIT;
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'SYSUSER')) 
       + (SELECT MIN(dv.fc_div_id) FROM fc_dimensions d, fc_dimvalues dv WHERE d.fc_dim_id = dv.fc_dim_id AND d.code = 'SYSUSER'),
       p.x + 1
FROM pivot p
WHERE p.x < 2000000;
COMMIT;
------
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val)
SELECT fc_vlm_seq.NEXTVAL, 
       (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'),
       p.x + 1,
       MOD(p.x, 1000)
FROM pivot p
WHERE p.x < 2000000;
COMMIT;
--INSERT INTO fc_valuesmsr(fc_vlm_id,fc_msr_id,rownumber,val)
--SELECT fc_vlm_seq.NEXTVAL, 
--       (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECCLNT'),
--       p.x + 1,
--       sin(MOD(p.x, 1000))
--FROM pivot p
--WHERE p.x < 2000000;
--COMMIT;


--------------------------------------
SELECT COUNT(*) FROM fc_valuesdim;
SELECT COUNT(*) FROM fc_valuesmsr;
SELECT COUNT(DISTINCT vm.rownumber) FROM fc_valuesmsr vm;

/*
SELECT d3, COUNT(*), SUM(m1), AVG(m1), SUM(m2), AVG(m2)
FROM (
    SELECT sel_dim_1.rownumber, sel_dim_1.val AS d1, sel_dim_2.val AS d2, sel_dim_3.val AS d3, sel_msr_1.val AS m1, sel_msr_2.val AS m2
    FROM 
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'DAY') sel_dim_1,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'HOUR') sel_dim_2,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'ACTCLASS') sel_dim_3,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'SYSUSER') sel_dim_4,
    (SELECT vm.val, vm.rownumber FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECTIME') sel_msr_1,
    (SELECT vm.val, vm.rownumber FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECCLNT') sel_msr_2
    WHERE sel_dim_1.rownumber = sel_dim_2.rownumber
    AND   sel_dim_1.rownumber = sel_dim_3.rownumber
    AND   sel_dim_1.rownumber = sel_dim_4.rownumber
    AND   sel_dim_1.rownumber = sel_msr_1.rownumber
    AND   sel_dim_1.rownumber = sel_msr_2.rownumber
    -- additional 'redundant' connections between all dimensions and measures rownumbers are helping optimize dbms queries 
    AND   sel_dim_2.rownumber = sel_dim_3.rownumber
    AND   sel_dim_2.rownumber = sel_dim_4.rownumber
    AND   sel_dim_2.rownumber = sel_msr_1.rownumber
    AND   sel_dim_2.rownumber = sel_msr_2.rownumber
    -- 
    AND   sel_dim_3.rownumber = sel_dim_4.rownumber
    AND   sel_dim_3.rownumber = sel_msr_1.rownumber
    AND   sel_dim_3.rownumber = sel_msr_2.rownumber
    --
    AND   sel_dim_4.rownumber = sel_msr_1.rownumber
    AND   sel_dim_4.rownumber = sel_msr_2.rownumber
    --
    AND   sel_msr_1.rownumber = sel_msr_2.rownumber
    -- filters
    --AND   sel_dim_1.val = '1'
    --AND   sel_dim_2.val = '2'
    --AND   sel_dim_2.val = 'CLASS_1'
    --AND   sel_dim_3.val = 'USER_1'
    --AND   sel_msr_1.val < 1000
) GROUP BY d3;
*/

SELECT d3, COUNT(*), SUM(m1), AVG(m1)
FROM (
    SELECT sel_dim_1.rownumber, sel_dim_1.val AS d1, sel_dim_2.val AS d2, sel_dim_3.val AS d3, sel_msr_1.val AS m1
    FROM 
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'DAY') sel_dim_1,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'HOUR') sel_dim_2,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'ACTCLASS') sel_dim_3,
    (SELECT dv.val, vd.rownumber FROM fc_dimensions d, fc_dimvalues dv, fc_valuesdim vd WHERE d.fc_dim_id = dv.fc_dim_id AND dv.fc_div_id = vd.fc_div_id AND d.code = 'SYSUSER') sel_dim_4,
    (SELECT vm.val, vm.rownumber FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECTIME') sel_msr_1
    WHERE sel_dim_1.rownumber = sel_dim_2.rownumber
    AND   sel_dim_1.rownumber = sel_dim_3.rownumber
    AND   sel_dim_1.rownumber = sel_dim_4.rownumber
    AND   sel_dim_1.rownumber = sel_msr_1.rownumber
    -- additional 'redundant' connections between all dimensions and measures rownumbers are helping optimize dbms queries 
    AND   sel_dim_2.rownumber = sel_dim_3.rownumber
    AND   sel_dim_2.rownumber = sel_dim_4.rownumber
    AND   sel_dim_2.rownumber = sel_msr_1.rownumber
    -- 
    AND   sel_dim_3.rownumber = sel_dim_4.rownumber
    AND   sel_dim_3.rownumber = sel_msr_1.rownumber
    --
    AND   sel_dim_4.rownumber = sel_msr_1.rownumber
    -- filters
    AND   sel_dim_1.val = '4'
    --AND   sel_dim_2.val = '2'
    --AND   sel_dim_2.val = 'CLASS_1'
    --AND   sel_dim_3.val = 'USER_1'
    --AND   sel_msr_1.val < 1000
) GROUP BY d3;




