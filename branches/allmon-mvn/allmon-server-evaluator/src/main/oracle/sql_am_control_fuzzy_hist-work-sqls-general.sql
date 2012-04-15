

DROP TABLE am_res_data;
CREATE TABLE am_res_data (
  ts   DATE,
  tss  NUMBER(6),
  r1   NUMBER(3,2),
  r2   NUMBER(3,2),
  r3   NUMBER(3,2),
  r4   NUMBER(3,2),
  r5   NUMBER(3,2),
  a1   NUMBER(3,2),
  a2   NUMBER(3,2),
  a3   NUMBER(3,2),
  a4   NUMBER(3,2),
  a5   NUMBER(3,2)
);

SELECT * FROM all_tables t WHERE t.table_name = upper('am_res_data');

SELECT 3 + avg(--nvl(dbms_lob.getlength(ts),0)+1 +
               nvl(vsize(tss),0)+1 +
               10*(nvl(vsize(r1),0)+1)
              ) "Total bytes per row"
FROM am_res_data WHERE tss = 1

--ALTER TABLE am_res_data ADD d NUMBER;

SELECT * FROM am_res_data FOR UPDATE;

CREATE TABLE am_pivot AS
WITH ones AS
   (SELECT 0 x FROM dual
    UNION SELECT 1 FROM dual
    UNION SELECT 2 FROM dual
    UNION SELECT 3 FROM dual
    UNION SELECT 4 FROM dual
    UNION SELECT 5 FROM dual
    UNION SELECT 6 FROM dual
    UNION SELECT 7 FROM dual
    UNION SELECT 8 FROM dual
    UNION SELECT 9 FROM dual)
    SELECT 100000*o100000.x + 10000*o10000.x + 1000*o1000.x + 100*o100.x + 10*o10.x + o1.x x
    FROM ones o1, ones o10, ones o100, ones o1000, ones o10000, ones o100000;

DELETE FROM am_res_data;

INSERT INTO am_res_data SELECT NULL, x, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 FROM am_pivot WHERE x < 4 * 60;

UPDATE am_res_data SET r1 = 0.5 + 0.5 * cos(0.1*tss), -- slow
                       r2 = 0.5 + 0.5 * sin(0.12*tss) * sin(0.23*tss) * sin(0.31*tss),
                       r3 = 0.5 + 0.5 * sin(0.12*tss) * sin(0.24*tss) * sin(0.31*tss),
                       r4 = 0.5 + 0.5 * sin(0.1*tss+5) * cos(0.51*tss), -- fase
                       r5 = 0.5 + 0.3 * atan(tss-200);
--SELECT atan(65) FROM dual 

SELECT * FROM am_res_data ORDER BY tss;

-- distance
SELECT d.*, 
       sqrt(r1*r1+r2*r2+r3*r3+r4*r4+r5*r5) AS dist
FROM am_res_data d
ORDER BY tss;


CREATE OR REPLACE FUNCTION am_fn_distance(pv_tss1 IN NUMBER, pv_tss2 IN NUMBER)
RETURN NUMBER IS 
  

END;

-- classification !!!!
-- similarity by clustering (i.e. euclidiean distance) - but only most close points are considered (not similar enought)
-- also, potentially search in sub-spaces, not considering certain dimensions (varying)

-- 1. find similar points of found segment points
-- find all similar point in hiper-cube of size 0.2 x 0.2 x 0.2 x ... 
SELECT rd.*, rds.tss 
FROM   am_res_data rd, am_res_data rds
WHERE  rds.tss IN (154) --, 154, 155) -- segment point(s)
AND    rd.r1 BETWEEN rds.r1 - 0.1 AND rds.r1 + 0.1 -- main dim
AND    rd.r2 BETWEEN rds.r2 - 0.1 AND rds.r2 + 0.1
--AND    rd.r3 BETWEEN rds.r3 - 0.1 AND rds.r3 + 0.1
AND    rd.r4 BETWEEN rds.r4 - 0.1 AND rds.r4 + 0.1
AND    rd.r5 BETWEEN rds.r5 - 0.1 AND rds.r5 + 0.1
ORDER BY rd.tss
;

-- r1 is a main dim - we check dependencis of others to this -- complexity: O(n*n/2)
--                                                                           ; count class ; count/
--                                                                           ;             ; (dim_rem+1)
-- removed dim none:               count = 4   (r1,r2,r3,r4,r5) -- all dims  ;    max      ; 4
-- removed dim r5 :                count = 5   (r1,r2,r3,r4)                 ;    max      ; 2.5
-- removed dim r5, r4 :            count = 21  (r1,r2,r3)                    ;             ; 10
-- removed dim r5, r4, r3 :        count = 31  (r1,r2)                       ;             ; 10          ; weakest
-- removed dim r5, r4, r3, r2 :    count = 58  (r1) -- main dim only         ;    min      ; 15          ; weakest
-- removed dim r5, r4, r2 :        count = 30  (r1,r3)                       ;             ; 10          ; weakest
-- removed dim r5, r3 :            count = 4   (r1,r2,r4)                    ;    max      ; 1.3         ; strongest
-- removed dim r5, r2 :            count = 6   (r1,r3,r4)                    ;             ; 2
-- removed dim r5, r4 :            count = 21  (r1,r2,r3)                    ;             ; 7
-- removed dim r4 :                count = 17  (r1,r2,r3,r5)                 ;             ; 8.5
-- removed dim r4, r3 :            count = 25  (r1,r2,r5)                    ;             ; 8.3
-- removed dim r4, r2 :            count = 23  (r1,r3,r5)                    ;             ; 7.7
-- removed dim r3 :                count = 4   (r1,r2,r4,r5)                 ;    max      ; 2           
-- removed dim r3, r2 :            count = 7   (r1,r5)                       ;             ; 1.75        ; strongest
-- removed dim r2 :                count = 6   (r1,r3,r4,r5)                 ;             ; 6


-- note: 
-- a. the longer list the more common the system condition 
-- b. 
-- cardinality
-- 

-- 2. check distribution of not considered (varying) dimensions - conclussion about behavoiur


SELECT * FROM am_res_data WHERE tss = 154

