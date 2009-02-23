-------------------------------------------------------------------------------------------------------------------------
-- inserting example data
-- -- -- metadata
/*
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Date and time', 'DATETIME');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'Action Class', 'ACTCLASS');
INSERT INTO fc_dimensions(fc_dim_id, dimname, code) VALUES (fc_dim_seq.NEXTVAL, 'System user', 'SYSUSER');

INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'Execution time', 'EXECTIME');
--INSERT INTO fc_measures(fc_msr_id, msrname, code) VALUES (fc_msr_seq.NEXTVAL, 'CPU Utlization USR', 'CPUUSR');

INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-01');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-02');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-03');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-04');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-05');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-06');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-07');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-08');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'DATETIME'), '2008-01-09');

INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_1');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_2');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_3');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_4');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_5');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_6');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_7');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_8');
INSERT INTO fc_dimvalues(fc_div_id, fc_dim_id, val) VALUES (fc_div_seq.NEXTVAL, (SELECT fc_dim_id FROM fc_dimensions WHERE code = 'ACTCLASS'), 'CLASS_9');


-- -- -- values -- space of facts
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '2008-01-01'), 1);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'CLASS_1'), 1);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '2008-01-01'), 2);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'CLASS_1'), 2);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '2008-01-01'), 3);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'CLASS_2'), 3);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '2008-01-02'), 4);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'CLASS_2'), 4);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = '2008-01-02'), 5);
INSERT INTO fc_valuesdim(fc_vld_id, fc_div_id, rownumber) VALUES (fc_vld_seq.NEXTVAL, (SELECT dv.fc_div_id FROM fc_dimvalues dv WHERE dv.val = 'CLASS_2'), 5);

-- -- -- values -- measures values
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), 1, 20);
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), 2, 40);
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), 3, 50);
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), 4, 75);
INSERT INTO fc_valuesmsr(fc_vlm_id, fc_msr_id, rownumber, val) VALUES (fc_vlm_seq.NEXTVAL, (SELECT fm.fc_msr_id FROM fc_measures fm WHERE fm.code = 'EXECTIME'), 5, 15);
*/

COMMIT;

-------------------------------------------------------------------------------------------------------------------------
-- selects
SELECT * FROM fc_dimensions;
SELECT * FROM fc_dimvalues;

SELECT * FROM fc_measures;

SELECT COUNT(*) FROM fc_valuesdim;
SELECT COUNT(*) FROM fc_valuesmsr;

-- all space
SELECT * 
FROM   fc_valuesdim vd, fc_valuesmsr vm
WHERE  vd.rownumber = vm.rownumber;

-- generic select nr 1
SELECT sel_dim_1.rownumber, sel_dim_1.val, sel_dim_2.val, sel_msr_1.val
FROM 
(SELECT d1.dimname, dv1.val, vd.* FROM fc_dimensions d1, fc_dimvalues dv1, fc_valuesdim vd WHERE d1.fc_dim_id = dv1.fc_dim_id AND dv1.fc_div_id = vd.fc_div_id AND d1.code = 'DATETIME') sel_dim_1,
(SELECT d1.dimname, dv1.val, vd.* FROM   fc_dimensions d1, fc_dimvalues dv1, fc_valuesdim vd WHERE d1.fc_dim_id = dv1.fc_dim_id AND dv1.fc_div_id = vd.fc_div_id AND d1.code = 'ACTCLASS') sel_dim_2,
(SELECT m.msrname, vm.* FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECTIME') sel_msr_1
WHERE sel_dim_1.rownumber = sel_dim_2.rownumber
AND   sel_dim_1.rownumber = sel_msr_1.rownumber;

-- generic select nr 2
SELECT rownumber, d1, d2, m1
FROM (
  SELECT vd1.rownumber,
         d1.dimname, dv1.val AS d1, 
         d2.dimname, dv2.val AS d2, 
         m1.msrname, vm1.val AS m1
  FROM   fc_dimensions d1, fc_dimvalues dv1, fc_valuesdim vd1, 
         fc_dimensions d2, fc_dimvalues dv2, fc_valuesdim vd2,
         fc_measures m1, fc_valuesmsr vm1
  WHERE  d1.fc_dim_id = dv1.fc_dim_id AND dv1.fc_div_id = vd1.fc_div_id AND d1.code = 'DATETIME' -- d1
  AND    d2.fc_dim_id = dv2.fc_dim_id AND dv2.fc_div_id = vd2.fc_div_id AND d2.code = 'ACTCLASS' -- d2
  AND    m1.fc_msr_id = vm1.fc_msr_id AND m1.code = 'EXECTIME' --m1
  -- connect rownums
  AND   vd1.rownumber = vd2.rownumber
  AND   vd1.rownumber = vm1.rownumber
);

-- grouping 
SELECT d2, COUNT(*), SUM(m1), AVG(m1)
FROM (
    SELECT sel_dim_1.rownumber, sel_dim_1.val AS d1, sel_dim_2.val AS d2, sel_msr_1.val AS m1
    FROM 
    (SELECT d1.dimname, dv1.val, vd.* FROM fc_dimensions d1, fc_dimvalues dv1, fc_valuesdim vd WHERE d1.fc_dim_id = dv1.fc_dim_id AND dv1.fc_div_id = vd.fc_div_id AND d1.code = 'DATETIME') sel_dim_1,
    (SELECT d1.dimname, dv1.val, vd.* FROM fc_dimensions d1, fc_dimvalues dv1, fc_valuesdim vd WHERE d1.fc_dim_id = dv1.fc_dim_id AND dv1.fc_div_id = vd.fc_div_id AND d1.code = 'ACTCLASS') sel_dim_2,
    (SELECT m.msrname, vm.* FROM fc_measures m, fc_valuesmsr vm WHERE m.fc_msr_id = vm.fc_msr_id AND m.code = 'EXECTIME') sel_msr_1
    WHERE sel_dim_1.rownumber = sel_dim_2.rownumber
    AND   sel_dim_1.rownumber = sel_msr_1.rownumber
    -- additional filters
    AND   sel_dim_1.val = '2008-01-2-1'
    --AND   sel_msr_1.val < 1000
) GROUP BY d2;

SELECT d2, COUNT(*), SUM(m1), AVG(m1)
FROM (
  SELECT rownumber, d1, d2, m1
  FROM (
    SELECT vd1.rownumber,
           d1.dimname, dv1.val AS d1, 
           d2.dimname, dv2.val AS d2, 
           m1.msrname, vm1.val AS m1
    FROM   fc_dimensions d1, fc_dimvalues dv1, fc_valuesdim vd1, 
           fc_dimensions d2, fc_dimvalues dv2, fc_valuesdim vd2,
           fc_measures m1, fc_valuesmsr vm1
    WHERE  d1.fc_dim_id = dv1.fc_dim_id AND dv1.fc_div_id = vd1.fc_div_id AND d1.code = 'DATETIME' -- d1
    AND    d2.fc_dim_id = dv2.fc_dim_id AND dv2.fc_div_id = vd2.fc_div_id AND d2.code = 'ACTCLASS' -- d2
    AND    m1.fc_msr_id = vm1.fc_msr_id AND m1.code = 'EXECTIME' --m1
    -- connect rownums
    AND   vd1.rownumber = vd2.rownumber
    AND   vd1.rownumber = vm1.rownumber
    -- additional filters
    --AND   dv1.val = '2008-01-07'
    --AND   vm1.val < 500
  )
) GROUP BY d2;

