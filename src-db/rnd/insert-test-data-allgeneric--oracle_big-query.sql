

SELECT d6, d7, COUNT(*), SUM(m1), AVG(m1)
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
    --AND   sel_dim_7.val = '7' -- hour
    AND   sel_dim_8.val = 8 -- minute
    --AND   sel_dim_2.val = '2'
    --AND   sel_dim_2.val = 'CLASS_1'
    --AND   sel_dim_3.val = 'USER_1'
    --AND   sel_msr_1.val < 1000
) GROUP BY d6, d7
ORDER BY 1, 2;



