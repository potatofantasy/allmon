﻿
-- Raw Metrics SQLS -------------------------------------------------------------------

select * from am_raw_metric r where r.resourcename = 'CPU User Time:';

-- lists of all attributes
select r.artifactcode, r.hostname, r.instancename, r.metrictypecode, r.resourcename, --r.sourcename,
       count(*) as rows_count
from am_raw_metric r 
group by r.artifactcode, r.hostname, r.instancename, r.metrictypecode, r.resourcename --r.sourcename
order by 1,2,3,4,5;

select r.artifactcode, r.hostname, r.instancename, r.metrictypecode, r.resourcename, --r.sourcename,
       count(*) as rows_count
from am_raw_metric r  
where r.artifactcode ='APP'
group by r.artifactcode, r.hostname, r.instancename, r.metrictypecode, r.resourcename --r.sourcename
order by 1,2,3,4,5;



-- lists of resources
select distinct r.resourcename from am_raw_metric r;

select r.resourcename, count(*), avg(r.metricvalue)
from am_raw_metric r --where r.resourcename = 'CPU User Time:'
group by r.resourcename;

-- resource minute aggregate
select to_char(r.ts, 'YYYY-MM-DD') as day,
       to_char(r.ts, 'YYYY-MM-DD HH24') as hour, 
       to_char(r.ts, 'YYYY-MM-DD HH24:MI') as mi, 
       count(*) count, 
       sum(r.metricvalue) as sum, -- for actions
       avg(r.metricvalue) as avg,
       min(r.metricvalue) as min,
       max(r.metricvalue) as max, 
       stddev(r.metricvalue) as stddev
  from am_raw_metric r
 where r.resourcename = 'Mem UsedPercent:' --:resourcename
 group by 
       to_char(r.ts, 'YYYY-MM-DD'),
       to_char(r.ts, 'YYYY-MM-DD HH24'), 
       to_char(r.ts, 'YYYY-MM-DD HH24:MI')
 order by mi;


-- seconds of metric values for given resource 
select to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as sec, 
       count(*) count, 
       sum(r.metricvalue) as sum, -- for actions
       avg(r.metricvalue) as avg,
       min(r.metricvalue) as min,
       max(r.metricvalue) as max, 
       stddev(r.metricvalue) as stddev
  from am_raw_metric r
 where r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
                        --'java.lang:type=Memory:HeapMemoryUsage/used'
                        --'DiskQueue:' --'Swap PageIn:' --'CPU User Time:' --'Processes Total:' --:resourcename
  and  r.ts >= to_date('2011-12-05 08:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  and  r.ts <= to_date('2011-12-05 23:00:00', 'YYYY-MM-DD HH24:MI:SS') 
 group by to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')
 order by sec;

-- SLAs ------------------------------------------------------------------------------
-- SLA calculation - (aggregation process - calculating synthetic resources)
select * from am_raw_metric where resourcename = 'SLA1: 1$ per every extra second over 2sec execution';
delete from am_raw_metric where resourcename = 'SLA1: 1$ per every extra second over 2sec execution';
insert into am_raw_metric(am_rme_id, 
                          artifactcode, hostname, hostip, instancename, entrypoint, --threadname,
                          metrictypecode, resourcename, sourcename,
                          ts, metricvalue)
select                    am_rme_seq.nextval, 
                          'SYNTH' as artifactcode, hostname, hostip, instancename, 'ENTRY' as entrypoint, 
                          'ACTCLS-SLA' as metrictypecode, 'SLA1: 1$ per every extra second over 2sec execution' as resourcename, sourcename, 
                          to_date(ts, 'YYYY-MM-DD HH24:MI:SS'), sla_value
from 
(select -- artifactcode, hostname, hostip, instancename, entrypoint, threadname,
       hostname, hostip, instancename, 
       r.resourcename as sourcename,
       -- ts:
       to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as ts,
       -- metricvalue:
       trunc(((sum(r.metricvalue) - 2000) / 1000), 2) as sla_value -- 1$ per every second over 2 sec
                                --count(*) count, sum(r.metricvalue) as sum, avg(r.metricvalue) as avg,
  from am_raw_metric r
 where r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
  --and  r.ts >= to_date('2011-12-05 08:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  --and  r.ts <= to_date('2011-12-05 23:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  and  r.metricvalue > 2000 -- longer than a second
 group by hostname, hostip, instancename, 
          r.resourcename,
          to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')
 order by ts) sel;


-- SLA #2 calculation - (aggregation process - calculating synthetic resources)
select * from am_raw_metric where resourcename = 'SLA2: 1$ extra for actions over 1sec execution';
delete from am_raw_metric where resourcename = 'SLA2: 1$ extra for actions over 1sec execution';
insert into am_raw_metric(am_rme_id, 
                          artifactcode, hostname, hostip, instancename, entrypoint, --threadname,
                          metrictypecode, resourcename, sourcename,
                          ts, metricvalue)
select                    am_rme_seq.nextval, 
                          'SYNTH' as artifactcode, hostname, hostip, instancename, 'ENTRY' as entrypoint, 
                          'ACTCLS-SLA' as metrictypecode, 'SLA2: 1$ extra for actions over 1sec execution' as resourcename, sourcename, 
                          to_date(ts, 'YYYY-MM-DD HH24:MI:SS'), sla_value
from (
 select -- artifactcode, hostname, hostip, instancename, entrypoint, threadname,
       hostname, hostip, instancename, 
       r.resourcename as sourcename,
       to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as ts,
       1 as sla_value -- just: 1$ extra for actions over 1sec execution
  from am_raw_metric r
 where r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
  --and  r.ts >= to_date('2011-12-05 08:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  --and  r.ts <= to_date('2011-12-05 23:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  and  r.metricvalue > 1000 -- longer than 1 second
 group by hostname, hostip, instancename, 
          r.resourcename,
          to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')
 order by ts) sel;

-- SLA #3 calculation - (aggregation process - calculating synthetic resources)
-- this SLA 
select * from am_raw_metric where resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 20ms';
select * from am_raw_metric where resourcename like 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]';
delete from am_raw_metric where resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 20ms';
insert into am_raw_metric(am_rme_id, 
                          artifactcode, hostname, hostip, instancename, entrypoint, --threadname,
                          metrictypecode, resourcename, sourcename,
                          ts, metricvalue)
select                    am_rme_seq.nextval, 
                          'SYNTH' as artifactcode, hostname, hostip, instancename, 'ENTRY' as entrypoint, 
                          'ACTCLS-SLA' as metrictypecode, 'SLA3: 10$ for every started second of an image processing longer by average than 20ms' as resourcename, sourcename, 
                          to_date(ts, 'YYYY-MM-DD HH24:MI:SS'), sla_value
from (
 select -- artifactcode, hostname, hostip, instancename, entrypoint, threadname,
       hostname, hostip, instancename, 
       'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]' as sourcename, --r.resourcename as sourcename,
       to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as ts,
       avg(r.metricvalue), sum(r.metricvalue), count(*),
       ceil(100 * (count(*) * sum(r.metricvalue) / 1000)) as sla_value -- 10$ for every started second of an image processing longer by average than 20ms
  from am_raw_metric r
 where r.resourcename like 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]'
  --and  r.ts >= to_date('2011-12-05 08:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  --and  r.ts <= to_date('2011-12-05 23:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  --and  r.metricvalue > 1000 -- longer than 1 second
 group by hostname, hostip, instancename, 
          'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]', --r.resourcename,
          to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')
 having avg(r.metricvalue) > 10 -- cummulative 1 second average aggregates over 10ms of execution
 order by ts) sel;


-- Correlations - statistical coeficients ---------------------------------------------------------
--- Correlation - calculating correlation coeficients
select to_char(ra.ts, 'YYYY-MM-DD HH24:MI'), trunc(to_char(ra.ts, 'SS')/15) as s15, --ra.metricvalue, ru.metricvalue, 
       corr(ra.metricvalue, ru.metricvalue), -- Pearson's correlation coefficient
       corr_s(ra.metricvalue, ru.metricvalue, 'TWO_SIDED_SIG') -- Spearman's rank correlation coefficient
from am_raw_metric ra, am_raw_metric ru
where ra.ts = ru.ts
and   ra.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
and   ra.metricvalue > 2000
and   ra.ts between to_date('2011-12-05 19:30', 'YYYY-MM-DD HH24:MI') and to_date('2011-12-05 22:00', 'YYYY-MM-DD HH24:MI')
and   ru.resourcename = 'DiskQueue:' --'CPU User Time:'
--and   ra.metricvalue != 0 and ru.metricvalue != 0
group by to_char(ra.ts, 'YYYY-MM-DD HH24:MI'), trunc(to_char(ra.ts, 'SS')/15) --, ra.metricvalue, ru.metricvalue
order by 1;

-- ..            checking data 
select ra.ts, ra.metricvalue action, ru.metricvalue cpu, ra.metricvalue / ru.metricvalue coef
from  am_raw_metric ra, am_raw_metric ru
where ra.ts = ru.ts
and   ra.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
and   ru.resourcename = 'DiskQueue:' --'CPU User Time:'
and   ra.metricvalue != 0 and ru.metricvalue != 0
and  to_char(ra.ts, 'YYYY-MM-DD HH24:MI') in ('2011-12-05 20:15') and ra.metricvalue > 2000 -- corr = 0,975
--and  to_char(ra.ts, 'YYYY-MM-DD HH24:MI') in ('2011-12-05 11:20') and ra.metricvalue > 2000 -- corr = 0,975
--and   to_char(ra.ts, 'YYYY-MM-DD HH24:MI') in ('2011-12-05 12:45') -- corr = 0,998
order by 1;

-- action class aggregates for /4 of minute
select -- artifactcode, hostname, hostip, instancename, entrypoint, threadname,
       hostname, hostip, instancename, 
       r.resourcename as sourcename,
       to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as ts, trunc(to_char(r.ts, 'SS')/15) as s15,
       sum(r.metricvalue)
  from am_raw_metric as r
  and r.ts >= to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  and  r.ts <= to_date('2011-12-05 21:00:00', 'YYYY-MM-DD HH24:MI:SS')  r.ts >= to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') 
  and  r.ts <= to_date('2011-12-05 21:00:00', 'YYYY-MM-DD HH24:MI:SS') 
 -- and  r.metricvalue > 2000 -- longer than a second
 group by hostname, hostip, instancename, 
          r.resourcename,
          to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS'), trunc(to_char(r.ts, 'SS')/15)
 order by ts;

-- lerp - linear interpolation (http://en.wikipedia.org/wiki/Linear_interpolation)
select s.*,
       case when val is null then (y0 + (tss - x0) * (y1 - y0)/(x1 - x0))
            else val end as lerp
from (
      select s.*,
             case when val is null then max(tss) over(order by aboveCnt range between 1 preceding and 1 preceding) else tss end as x0,
             case when val is null then min(tss) over(order by belowCnt range between 1 preceding and 1 preceding) else tss end as x1,
             case when val is null then max(Val) over(order by aboveCnt range between 1 preceding and 1 preceding) else val end as y0, --Lag1,
             case when val is null then max(Val) over(order by belowCnt range between 1 preceding and 1 preceding) else val end as y1 --Lead1
      from (select s.*, 
                   count(val) over(order by tss rows between unbounded preceding and 1 preceding) aboveCnt,
                   count(Val) over(order by tss rows between 1 following and unbounded following) belowCnt        
          from (
                select r.*, r.metricvalue as val, p.x as tss
                from (select r.*, to_char(r.ts,'DD') * 24 * 3600 + to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r) r, am_pivot p
                where r.sssss(+) = p.x
                and  r.resourcename(+) = 'CPU Combined:'
                and  p.x > 504000 and p.x <= 504000 + 3600
                --and  r.ts(+) <= to_date('2011-12-05 21:00:00', 'YYYY-MM-DD HH24:MI:SS') 
                --and r.ts(+) >= to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') 
          ) s) s) s
order by tss;
--- lerp end

---------------------------------------------------------------------------------------------------------------
---- lerp + correlation -- populating temporary tables

drop table temp_cpu;
create table temp_cpu as 
select s.*,
       case when val is null then (y0 + (tss - x0) * (y1 - y0)/(x1 - x0)) else val end as lerp -- linear interpolation
from (
      select s.*,
             case when val is null then max(tss) over(order by aboveCnt range between 1 preceding and 1 preceding) else tss end as x0,
             case when val is null then min(tss) over(order by belowCnt range between 1 preceding and 1 preceding) else tss end as x1,
             case when val is null then max(Val) over(order by aboveCnt range between 1 preceding and 1 preceding) else val end as y0, --Lag1,
             case when val is null then max(Val) over(order by belowCnt range between 1 preceding and 1 preceding) else val end as y1 --Lead1
      from (select s.*, 
                   count(val) over(order by tss rows between unbounded preceding and 1 preceding) aboveCnt,
                   count(Val) over(order by tss rows between 1 following and unbounded following) belowCnt        
          from (
                -- ==> temp_cpu
                select to_char(r.ts, 'HH24:MI:SS') as time, avg(r.metricvalue) as val, p.x as tss --r.*, r.metricvalue as val, p.x as tss
                from (select r.*, to_char(r.ts,'DD') * 24 * 3600 + to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r) r, am_pivot p
                where r.sssss(+) = p.x
                and  r.resourcename(+) = 'CPU Combined:' 
                and  p.x > 504000 and p.x <= 504000 + 3600
                group by p.x, to_char(r.ts, 'HH24:MI:SS') 
                order by p.x
                -- ==> temp_diskqueue
                /*select to_char(r.ts, 'HH24:MI:SS') as time, avg(r.metricvalue) as val, p.x as tss
                from (select r.*, to_char(r.ts,'DD') * 24 * 3600 + to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r) r, am_pivot p
                where r.sssss(+) = p.x
                and  r.resourcename(+) = 'DiskQueue:'
                and  p.x > 504000 and p.x <= 504000 + 3600
                group by p.x, to_char(r.ts, 'HH24:MI:SS') 
                order by p.x*/
          ) s) s) s
order by tss;

drop table temp_sla3;
create table temp_sla3 as 
  select p.x as tss, to_char(r.ts, 'HH24:MI:SS') as time, sum(r.metricvalue) as sum, avg(r.metricvalue) as avg, 
         sum(case when r.metricvalue is null then 0 else 1 end) as cnt
  from (select r.*, to_char(r.ts,'DD') * 24 * 3600 + to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r) r, am_pivot p
  where r.sssss(+) = p.x
  -- ==> temp_action --and  r.resourcename(+) = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
  -- ==> temp_sla -- and  resourcename(+) = 'SLA1: 1$ per every extra second over 2sec execution'
  -- ==> temp_sla3 
  and  resourcename(+) = 'SLA3: 10$ for every started second of an image processing longer by average than 20ms'
  and  p.x > 504000 and p.x <= 504000 + 3600
  group by p.x, to_char(r.ts, 'HH24:MI:SS')
  order by p.x;

select * from temp_sla3 order by 1;

-- correlation
-- a. select data, without aggregation - quite noisy!!
select trunc(a.tss / 15) * 15, c.tss, a.time, a.sum as asum, 1000*a.cnt as acnt, 
       round(10000*c.lerp, 2) as cpu, round(1000*d.lerp,2) as dqueue
from temp_action a, temp_cpu c, temp_diskqueue d
where a.tss = c.tss and a.tss = d.tss
order by 1;

-- b. aggregate and calculate pearson on group by time (a. 12 and b. 4 times a minute)
select trunc(a.tss / 5) * 5, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
       round(avg(a.sum), 2) as action, round(avg(5000*c.lerp), 2) as cpu, round(avg(1000*d.lerp),2) as dqueue,
       -- Pearson's correlation coefficient
       corr(a.sum, c.lerp) as pcorr_a_c, corr(a.sum, d.val) as pcorr_a_d,
       case when corr(a.sum, c.lerp) > 0.7 then round(corr(a.sum, c.lerp), 2) else 0 end as pearson_a_c,
       case when corr(a.sum, d.lerp) > 0.7 then round(corr(a.sum, d.lerp), 2) else 0 end as pearson_a_d
       --corr_s(a.val, c.val, 'TWO_SIDED_SIG') -- Spearman's rank correlation coefficient
from temp_action a, temp_cpu c, temp_diskqueue d
where a.tss = c.tss and a.tss = d.tss
group by trunc(a.tss / 5) * 5
order by 1;
-- bb. - 4 aggregates per minute
select trunc(a.tss / 15) * 15, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
       round(avg(a.sum), 2) as action, round(avg(5000*c.lerp), 2) as cpu, round(avg(1000*d.lerp),2) as dqueue,
       -- Pearson's correlation coefficient
       corr(a.sum, c.lerp) as pcorr_a_c, corr(a.sum, d.val) as pcorr_a_d,
       case when corr(a.sum, c.lerp) > 0.7 then round(corr(a.sum, c.lerp), 2) else 0 end as pearson_a_c,
       case when corr(a.sum, d.lerp) > 0.7 then round(corr(a.sum, d.lerp), 2) else 0 end as pearson_a_d
       --corr_s(a.val, c.val, 'TWO_SIDED_SIG') -- Spearman's rank correlation coefficient
from temp_action a, temp_cpu c, temp_diskqueue d
where a.tss = c.tss and a.tss = d.tss
group by trunc(a.tss / 15) * 15
order by 1;

-- c. ???
select trunc(a.tss / 15) * 15, 
       c.tss, a.time, a.sum as action, 10000 * c.lerp as cpu, 1000*d.lerp as dqueue,
       --avg(a.val) as action, avg(5000*c.val) as cpu, avg(1000*d.val) as dqueue,
       -- Pearson's correlation coefficient
       round(corr(a.sum, c.lerp) over (partition by trunc(a.tss / 15) * 15 order by a.tss), 2) as pcorr_a_c, 
       round(corr(a.sum, d.lerp) over (partition by trunc(a.tss / 15) * 15 order by a.tss), 2) as pcorr_a_d
--       case when corr(a.val, c.val) > 0.7 then round(corr(a.val, c.val), 2) else 0 end as pearson_a_c,
--       case when corr(a.val, d.val) > 0.7 then round(corr(a.val, d.val), 2) else 0 end as pearson_a_d
       --corr_s(a.val, c.val, 'TWO_SIDED_SIG') -- Spearman's rank correlation coefficient
from temp_action a, temp_cpu c, temp_diskqueue d
where a.tss = c.tss and a.tss = d.tss
order by 1;

----------------------------------------------
-- detecting slopes - differential , 1st and 2nd derivative
-- 4 aggregate values per minute
select t, action, daction, (daction - lead(daction, 1, 0) OVER (ORDER BY t DESC)) /(1) as d2action
from (
    select t, action, (action - lead(action, 1, 0) OVER (ORDER BY t DESC)) /(1) as daction,
    from (
        select trunc(a.tss / 15) * 15 as t, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
               case when avg(a.sum) is not null then round(avg(a.sum), 2) else 0 end as action, round(avg(5000*c.lerp), 2) as cpu, round(avg(1000*d.lerp),2) as dqueue
        from temp_action a, temp_cpu c, temp_diskqueue d
        where a.tss = c.tss and a.tss = d.tss
        and   a.tss < 504360
        group by trunc(a.tss / 15) * 15))
order by 1;

-- za pomoca dyskretnej funkcji splotu; dx:= [−1/2, 0, +1/2], d^2x:= [+1, −2, +1] -- jakos nie dziala
select t, action, (action - lead(action, 1, 0) OVER (ORDER BY t DESC)) as daction,
       (-0.5 * lead(action, 1, 0) OVER (ORDER BY t DESC)) + (0.5 * lag(action, 1, 0) OVER (ORDER BY t DESC)) as mdaction
from (
    select trunc(a.tss / 15) * 15 as t, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
           case when avg(a.sum) is not null then round(avg(a.sum), 2) else 0 end as action, round(avg(5000*c.lerp), 2) as cpu, round(avg(1000*d.lerp),2) as dqueue
    from temp_action a, temp_cpu c, temp_diskqueue d
    where a.tss = c.tss and a.tss = d.tss
    and   a.tss < 504360
    group by trunc(a.tss / 15) * 15)
order by 1;

-- first derivative tresholding: ==> finding beggining and end of a segment
----  gradient approach to the problem: analysig first and,or second derivative (first: min/max, second: zero values) 
----  + thresholding (first derivative by 30% of 5 minutes avg action time value) + noise reduction (avg, 15 sec window) 
select t, action, daction, d2action, avga,
       case when daction > 0.3*avga then 1000 when daction < -0.3*avga then -1000 else 0 end as e1 --and abs(d2action) > 100 -- future
from (
    select t, action, daction, (daction - lead(daction, 1, 0) OVER (ORDER BY t DESC)) /(1) as d2action,
           (select avg(a.sum) from temp_action a where a.tss between t - 5*60 and t + 5*60) as avga -- avg action time in 5mins neighberhood
    from (
        select t, action, (action - lead(action, 1, 0) OVER (ORDER BY t DESC)) /(1) as daction 
        from (
            select trunc(a.tss / 15) * 15 as t, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
                   case when avg(a.sum) is not null then round(avg(a.sum), 2) else 0 end as action, round(avg(5000*c.lerp), 2) as cpu, round(avg(1000*d.lerp),2) as dqueue
            from temp_action a, temp_cpu c, temp_diskqueue d
            where a.tss = c.tss and a.tss = d.tss
            --and   a.tss < 504360
            group by trunc(a.tss / 15) * 15)))
order by 1;

------------------------------------------------- 
-- finding segments, base on events found by edge detection
select t, action, daction, d2action, mavga, actione1,
       cpu, dcpu, d2cpu, mavgc,
       case when (case when actione1 is null then max(actione1) over(order by aboveCnt range between 1 preceding and 1 preceding) else actione1 end) > 0 then 1000 else 0 end as actionseg1,
       --case when actione1 is null then max(actione1) over(order by belowCnt range between 1 preceding and 1 preceding) else actione1 end as Lead1
       case when (case when cpue1 is null then max(cpue1) over(order by cpuaboveCnt range between 1 preceding and 1 preceding) else cpue1 end) > 0 then 1000 else 0 end as cpuseg1
from (select t, action, daction, d2action, mavga, actione1,
             cpu, dcpu, d2cpu, mavgc, cpue1,
             count(actione1) over(order by t rows between unbounded preceding and 1 preceding) aboveCnt,
             count(actione1) over(order by t rows between 1 following and unbounded following) belowCnt, 
             count(cpue1) over(order by t rows between unbounded preceding and 1 preceding) cpuaboveCnt,
             count(cpue1) over(order by t rows between 1 following and unbounded following) cpubelowCnt 
      from (
            select t, action, daction, d2action, mavga,
                   cpu, dcpu, d2cpu, mavgc,
                   case when daction > 0.3*mavga then 1000 when daction < -0.3*mavga then -1000 else null end as actione1, --and abs(d2action) > 100 -- future
                   case when dcpu > 0.3*mavgc then 1000 when dcpu < -0.3*mavgc then -1000 else null end as cpue1 --and abs(d2action) > 100 -- future
            from (
                select t, action, daction, (daction - lead(daction, 1, 0) OVER (ORDER BY t DESC)) /(1) as d2action,
                       cpu, dcpu, (dcpu - lead(dcpu, 1, 0) OVER (ORDER BY t DESC)) /(1) as d2cpu,
                       (select avg(a.sum) from temp_action a where a.tss between t - 5*60 and t + 5*60) as mavga, -- moving avg action time in 5mins neighberhood
                       (select avg(c1.lerp) from temp_cpu c1 where c1.tss between t - 5*60 and t + 5*60) as mavgc
                from (
                    select t, action, (action - lead(action, 1, 0) OVER (ORDER BY t DESC)) /(1) as daction,
                              cpu, (cpu - lead(cpu, 1, 0) OVER (ORDER BY t DESC)) /(1) as dcpu
                    from (
                        select trunc(a.tss / 15) * 15 as t, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
                               case when avg(a.sum) is not null then round(avg(a.sum), 2) else 0 end as action, 
                               case when avg(c.lerp) is not null then round(avg(5000*c.lerp), 2) else 0 end as cpu, round(avg(1000*d.lerp),2) as dqueue
                        from temp_action a, temp_cpu c, temp_diskqueue d
                        where a.tss = c.tss and a.tss = d.tss
                        --and   a.tss < 504360
                        group by trunc(a.tss / 15) * 15)))
            ))
order by 1;

-- segmentation based on action tresholding (non derivative) - threshold is 30% of moving average in 5 minutes neigberhood
select t, action, cpu, mavga, mavgc, mminc, cpu - mminc,
       case when action > 0.3*mavga then 10000 end as actione1, 
       case when cpu - mminc > 0.3*mavgc then 10000 else null end as cpue1 -- cpu - mminc : is to lower the signal level
from (
    select t, action, cpu,
           (select avg(a1.sum) from temp_action a1 where a1.tss between t - 5*60 and t + 5*60) as mavga, -- moving avg action time in 5mins neighberhood
           5000*(select avg(c1.lerp) from temp_cpu c1 where c1.tss between t - 5*60 and t + 5*60) as mavgc, -- moving avg
           5000*(select min(c1.lerp) from temp_cpu c1 where c1.tss between t - 5*60 and t + 5*60) as mminc -- moving min cpu level
    from (
        select trunc(a.tss / 15) * 15 as t, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
               case when avg(a.sum) is not null then round(avg(a.sum), 2) else 0 end as action, round(avg(5000*c.lerp), 2) as cpu, round(avg(1000*d.lerp),2) as dqueue
        from temp_action a, temp_cpu c, temp_diskqueue d
        where a.tss = c.tss and a.tss = d.tss
        --and   a.tss < 504360
        group by trunc(a.tss / 15) * 15))
order by 1;

-- comparing times of events based on found segments (in 15 seconds aggregates) - constants are added to normalize values
-- use temp tab
drop table temp_matching ;
insert into temp_matching () 
--create table temp_matching_action as
select  t, action, action_cnt, cpu, dqueue, sla, sla3, sla + sla3 as sla_sum, actione1, cpue1, actioncpue1, actionorcpue1,
        cpusat, cpusatrule,
        case when cpusatrule is not null then sla + action_cnt/50 * 2 else sla end as sla_new,
        case when cpusatrule is not null then 0 else sla3 end as sla3_new -- quite optimistic, assuming all images will have resources to process fast 
from (
    select  t, action, action_cnt, cpu, dqueue, sla, sla3, actione1, cpue1,
            case when actione1 is not null and cpue1 is not null then 9800 end as actioncpue1, 
            case when actione1 is not null or cpue1 is not null then 9800 end as actionorcpue1,
            case when cpu > 5000*0.7 then 10100 else null end as cpusat,
            case when dqueue > 5000*0.7 then 10200 else null end as iosat, -- is rare and misses a few good actione1 areas
            case when actione1 is not null and cpue1 is not null and cpu > 5000*0.7 then 10000 else null end as cpusatrule
    from (
        select t, action, action_cnt, cpu, dqueue, sla, sla3, --mavga, mavgc, mminc, cpu - mminc,
               case when action > 0.3*mavga then 9500 end as actione1, -- actrion events 
               case when cpu - mminc > 0.7*mavgc then 9750 else null end as cpue1 -- cpu events
        from (
            select t, action, action_cnt, cpu, dqueue, sla, sla3,
                   (select avg(a1.sum) from temp_action a1 where a1.tss between t - 5*60 and t + 5*60) as mavga, -- moving avg action time in 5mins neighberhood
                   5000*(select avg(c1.lerp) from temp_cpu c1 where c1.tss between t - 5*60 and t + 5*60) as mavgc,
                   5000*(select min(c1.lerp) from temp_cpu c1 where c1.tss between t - 5*60 and t + 5*60) as mminc, -- moving min cpu
                   200*(select avg(s1.sum) from temp_sla s1 where s1.tss between t - 5*60 and t + 5*60) as mavgs
            from (
                select trunc(a.tss / 15) * 15 as t, --c.tss, a.time, a.val as action, 10000 * c.val as cpu
                       case when sum(a.sum) is not null then round(sum(0.1*a.sum), 2) else 0 end as action, 
                       case when sum(a.cnt) is not null then round(sum(50*a.cnt), 2) else 0 end as action_cnt, 
                       round(avg(5000*c.lerp), 2) as cpu, round(avg(500*d.lerp),2) as dqueue, 
                       case when sum(s.sum) is not null then round(sum(200*s.sum), 2) else 0 end as sla,
                       case when sum(s3.sum) is not null then round(sum(100*s3.sum), 2) else 0 end as sla3
                from temp_action a, temp_cpu c, temp_diskqueue d, temp_sla s, temp_sla3 s3
                where a.tss = c.tss and a.tss = d.tss and a.tss = s.tss and a.tss = s3.tss
                --and   a.tss < 504360
                group by trunc(a.tss / 15) * 15))
    ))
    order by 1;
---


-----------------------------------------------------------------------------------------------------------
declare 
    ret number;
begin 
    ret := AM_CONTROL_FUZZY.prepare2(to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]', 
        'CPU Combined:', 
        'SLA1: 1$ per every extra second over 2sec execution' );
end;

select * from am_raw_metric a where a.resourcename =  'CPU Combined:' and a.ts >= to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS')
order by 1;

select * from temp_action order by 1;
select  *from temp_res order by 1;
select * from temp_sla order by 1;


select * from temp_norm_15sec_aggs order by t;

-- dependency coefficient
    select count(actionandrese) / count(actionorrese) * count(satandactionandrese) / count(actionandrese) as O,
           count(actionandrese) / count(actionorrese), count(satandactionandrese) / count(actionandrese)
    from (
        select t, action, res, sla,
               case when actione is not null and rese is not null then 1 end as actionandrese, 
               case when actione is not null or rese is not null then 1 end as actionorrese,
               case when sate = 1 and actione = 1 and rese = 1 then 1 end as satandactionandrese
        from temp_norm_15sec_aggs);

-- sla change evaluation 
    select  t, action, action_cnt, cpu, dqueue, sla, sla3, sla + sla3 as sla_sum, actione1, cpue1, actioncpue1, actionorcpue1,
            cpusat, cpusatrule,
            case when sate = 1 then sla + action_cnt * 2 else sla end as sla_new,
            case when sate = 1 then 0 else sla3 end as sla3_new -- quite optimistic, assuming all images will have resources to process fast 
    select * 
    from temp_norm_15sec_aggs;


---------------------------------------------------------------------------------------------------------------
-- calculate dependency coefficients
--select * from temp_matching_action t;
select count(*) as window_aggs_count, 
       sum(t.sla), sum(sla3),
       count(actione1), count(cpue1), count(actioncpue1), count(actionorcpue1), count(t.cpusat), count(t.cpusatrule),
       count(actioncpue1) / count(actionorcpue1), count(t.cpusatrule) / count(actioncpue1)
from temp_matching_action t;


select t, action_avg, action_cumedist,
       case when action_cumedist < 0.5 then 1 when action_cumedist < 0.75 then 1/(action_cumedist/0.2) else 0 end as ok, -- fuzzy set scopes
       case when action_cumedist > 0.75 then 1 when action_cumedist > 0.5 then 1/(action_cumedist/0.2) else 0 end as sat
from (
   select -- calculate coefficients  -- 240 - count of time aggregates in the moving window
         200*trunc(cpu/200) / 5000 as t, 
         --round(action/100), 
         avg(action) as action_avg, 
         --avg(cpu), 
         avg(sla), avg(sla3), avg(dqueue),
         sum(avg(action)) over (order by 200*trunc(cpu/200) / 5000 range unbounded preceding) / sum(avg(action)) over () as action_cumedist
         --sum(avg(cpu)) over (order by round(action/100) range unbounded preceding) / sum(avg(cpu)) over () as cpu_cumedist
         --trunc(action/100), avg(cpu), avg(sla), avg(sla3)
         --count(actione1)/240, count(cpue1)/240, count(cpusat)/240, count(cpusatrule)/240,
         --count(cpusatrule) / count(cpusat) as rule_strength
   from (
      temp_matching_action
   )
   group by  200*trunc(cpu/200) / 5000
             --round(action/100)
) 
order by 1 desc;

select ceil(r.metricvalue/1000), sum(r.metricvalue)/1000, count(*)
from am_raw_metric r 
where r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
and   r.entrypoint = 'EXIT'
group by ceil(r.metricvalue/1000) order by 1 asc;

select r.ts, r.metricvalue
from am_raw_metric r 
where r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
and   r.entrypoint = 'EXIT'


select tss, lerp from temp_cpu order by tss


select * from am_raw_metric a where a.resourcename =  'CPU Combined:' and a.ts >= to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS')
order by 1;

------------------------------------------------------------------------------------------------------------------------
-- Running Load Process of Raw Metrics to allmetric schema -------------------------------------------------------------

begin
am_allmetric_mngr.raw_load_to_allmetric(
       p_i_datetime_start => sysdate - 365, p_i_datetime_end => sysdate);

end;

-- 

select count(*) from am_raw_metric r 


