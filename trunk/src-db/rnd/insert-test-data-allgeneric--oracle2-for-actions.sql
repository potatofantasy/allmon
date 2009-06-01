
--------------------------------------
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date and time', 'DATETIME');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Action Class', 'ACTCLASS');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'System user', 'SYSUSER');

INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'Execution time', 'EXECTIME');
INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'Execution time2', 'EXECCLNT');
COMMIT;

--INSERT INTO fc_dimkeys(fc_dik_id, fc_dim_id, keyname, code) VALUES (fc_dik_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), 'Year', 'YEAR');
--INSERT INTO fc_dimkeys(fc_dik_id, fc_dim_id, keyname, code) VALUES (fc_dik_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), 'Month', 'MONTH');
INSERT INTO fc_dimkeys(fc_dik_id, fc_dim_id, keyname, code) VALUES (fc_dik_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), 'Day', 'DAY');
INSERT INTO fc_dimkeys(fc_dik_id, fc_dim_id, keyname, code) VALUES (fc_dik_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), 'Hour', 'HOUR');
INSERT INTO fc_dimkeys(fc_dik_id, fc_dim_id, keyname, code) VALUES (fc_dik_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'Action', 'ACTION');
INSERT INTO fc_dimkeys(fc_dik_id, fc_dim_id, keyname, code) VALUES (fc_dik_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'SYSUSER'), 'User', 'USER');
COMMIT;

DELETE FROM fc_dimkeyvalues;
BEGIN
  FOR dayno IN 1..31 LOOP
    INSERT INTO fc_dimkeyvalues(fc_dkv_id, fc_dik_id, val) VALUES (fc_dkv_seq.NEXTVAL, (SELECT fc_dik_id FROM fc_dimkeys WHERE code = 'DAY'), dayno);
  END LOOP;
  FOR hourno IN 0..23 LOOP
    INSERT INTO fc_dimkeyvalues(fc_dkv_id, fc_dik_id, val) VALUES (fc_dkv_seq.NEXTVAL, (SELECT fc_dik_id FROM fc_dimkeys WHERE code = 'HOUR'), hourno);
  END LOOP;
END;
BEGIN
  FOR classno IN 1..2000 LOOP
    INSERT INTO fc_dimkeyvalues(fc_dkv_id, fc_dik_id, val) VALUES (fc_dkv_seq.NEXTVAL, (SELECT fc_dik_id FROM fc_dimkeys WHERE code = 'ACTION'), 'CLASS_'||classno);
  END LOOP;
END;
BEGIN
  FOR userno IN 1..1000 LOOP
    INSERT INTO fc_dimkeyvalues(fc_dkv_id, fc_dik_id, val) VALUES (fc_dkv_seq.NEXTVAL, (SELECT fc_dik_id FROM fc_dimkeys WHERE code = 'USER'), 'USER_'||userno);
  END LOOP;
END;
COMMIT;   

--------------------------------------
--INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '2008-01-1-0'), 1);
--INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'CLASS_1'), 1);
--INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'USER_12'), 1);
--INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), 1, 7174.8616224958305);
DELETE FROM fc_valuesdim;
DELETE FROM fc_valuesmsr;
COMMIT;
------ 
INSERT INTO fc_valuesdim(fc_vld_id, fc_dkv_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'DAY')) 
       + (SELECT MIN(dkv.fc_dkv_id) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'DAY'),
       p.x + 1
FROM pivot p
WHERE p.x < 5000000;
COMMIT;
INSERT INTO fc_valuesdim(fc_vld_id, fc_dkv_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'HOUR')) 
       + (SELECT MIN(dkv.fc_dkv_id) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'HOUR'),
       p.x + 1
FROM pivot p
WHERE p.x < 5000000;
COMMIT;
INSERT INTO fc_valuesdim(fc_vld_id, fc_dkv_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'ACTION')) 
       + (SELECT MIN(dkv.fc_dkv_id) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'ACTION'),
       p.x + 1
FROM pivot p
WHERE p.x < 5000000;
COMMIT;
INSERT INTO fc_valuesdim(fc_vld_id, fc_dkv_id, rownumber)
SELECT fc_vld_seq.NEXTVAL, 
       MOD(p.x, (SELECT COUNT(*) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'USER')) 
       + (SELECT MIN(dkv.fc_dkv_id) FROM fc_dimkeyvalues dkv, fc_dimkeys dk WHERE dkv.fc_dik_id = dk.fc_dik_id AND dk.code = 'USER'),
       p.x + 1
FROM pivot p
WHERE p.x < 5000000;
COMMIT;

------
INSERT INTO fc_valuesmsr(fc_vlm_id,fc_msr_id,rownumber,val)
SELECT fc_vlm_seq.NEXTVAL, 
       (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'),
       p.x + 1,
       MOD(p.x, 1000)
FROM pivot p
WHERE p.x < 5000000;
COMMIT;
INSERT INTO fc_valuesmsr(fc_vlm_id,fc_msr_id,rownumber,val)
SELECT fc_vlm_seq.NEXTVAL, 
       (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECCLNT'),
       p.x + 1,
       sin(MOD(p.x, 1000))
FROM pivot p
WHERE p.x < 5000000;
COMMIT;


--------------------------------------
SELECT COUNT(*) FROM fc_valuesdim;
SELECT COUNT(*) FROM fc_valuesmsr;
SELECT COUNT(DISTINCT vm.rownumber) FROM fc_valuesmsr vm;

-----
SELECT d2, COUNT(*), SUM(m1), AVG(m1), SUM(m2), AVG(m2)
FROM (
    SELECT sel_dim_1_k1.rownumber, sel_dim_1_k1.val AS d1_k1, sel_dim_1_k2.val AS d1_k2, sel_dim_2.val AS d2, sel_dim_3.val AS d3, sel_msr_1.val AS m1, sel_msr_2.val AS m2
    FROM 
    (SELECT dk.keyname, dkv.val, vd.rownumber FROM fc_dimkeys dk, fc_dimkeyvalues dkv, fc_valuesdim vd WHERE dk.code = 'DAY' AND dk.fc_dik_id = dkv.fc_dik_id AND dkv.fc_dkv_id = vd.fc_dkv_id) sel_dim_1_k1,
    (SELECT dk.keyname, dkv.val, vd.rownumber FROM fc_dimkeys dk, fc_dimkeyvalues dkv, fc_valuesdim vd WHERE dk.code = 'HOUR' AND dk.fc_dik_id = dkv.fc_dik_id AND dkv.fc_dkv_id = vd.fc_dkv_id) sel_dim_1_k2,
    (SELECT dk.keyname, dkv.val, vd.rownumber FROM fc_dimkeys dk, fc_dimkeyvalues dkv, fc_valuesdim vd WHERE dk.code = 'ACTION' AND dk.fc_dik_id = dkv.fc_dik_id AND dkv.fc_dkv_id = vd.fc_dkv_id) sel_dim_2,
    (SELECT dk.keyname, dkv.val, vd.rownumber FROM fc_dimkeys dk, fc_dimkeyvalues dkv, fc_valuesdim vd WHERE dk.code = 'USER' AND dk.fc_dik_id = dkv.fc_dik_id AND dkv.fc_dkv_id = vd.fc_dkv_id) sel_dim_3,
    (SELECT m.msrname, vm.* FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECTIME') sel_msr_1,
    (SELECT m.msrname, vm.* FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECCLNT') sel_msr_2
    WHERE sel_dim_1_k1.rownumber = sel_dim_1_k2.rownumber
    AND   sel_dim_1_k1.rownumber = sel_dim_2.rownumber
    AND   sel_dim_1_k1.rownumber = sel_dim_3.rownumber
    AND   sel_dim_1_k1.rownumber = sel_msr_1.rownumber
    AND   sel_dim_1_k1.rownumber = sel_msr_2.rownumber
    -- additional 'redundant' connections between all dimensions and measures rownumbers are helping optimize dbms queries 
    AND   sel_dim_1_k2.rownumber = sel_dim_2.rownumber
    AND   sel_dim_1_k2.rownumber = sel_dim_3.rownumber
    AND   sel_dim_1_k2.rownumber = sel_msr_1.rownumber
    AND   sel_dim_1_k2.rownumber = sel_msr_2.rownumber
    --
    AND   sel_dim_2.rownumber = sel_dim_3.rownumber
    AND   sel_dim_2.rownumber = sel_msr_1.rownumber
    AND   sel_dim_2.rownumber = sel_msr_2.rownumber
    --
    --AND   sel_dim_3.rownumber = sel_dim_1.rownumber
    AND   sel_dim_3.rownumber = sel_dim_2.rownumber
    --AND   sel_dim_3.rownumber = sel_msr_1.rownumber
    AND   sel_dim_3.rownumber = sel_msr_2.rownumber
    --
    AND   sel_msr_1.rownumber = sel_msr_2.rownumber
    -- filters
    AND   sel_dim_1_k1.val = '1'
    AND   sel_dim_1_k2.val = '3'
    --AND   sel_dim_1.val IN ('2008-01-1-1', '2008-01-1-2', '2008-01-1-3')
    --AND   sel_dim_2.val = 'CLASS_1'
    --AND   sel_dim_3.val = 'USER_1'
    --AND   sel_msr_1.val < 1000
) GROUP BY d2;



