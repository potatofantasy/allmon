
--delete from am_raw_metric r where r.metrictypecode = 'ACTCLS-SLA';
--delete from am_raw_metric r where r.resourcename = 'SLA1: 100$ for every started second of an image processing longer by average than 20ms'

-----------------------------------------------------------------------------------------------------------
declare 
    ret number;
begin 
    -- update SLA1
    am_control_fuzzy.update_sla(
         to_date('2012-01-03 23:00:00', 'YYYY-MM-DD HH24:MI:SS'), --p_i_window_start => 
         'SLA1: 1$ per every extra second over 2sec execution', --p_i_sla_resource_name => 
         'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]', --p_i_base_resource_like => 
         ' (case when (sum(metricvalue) - 2000) / 1000 < 5 then (sum(metricvalue) - 2000) / 1000 else 5 end) ', --p_i_select_sla_value_phrase =>  -- but no more than 5$ penalty
         'metricvalue > 2000', --p_i_where_metric_phrase => -- actions longer than 2 secs
         '1=1'); --p_i_having_phrase => 
    -- update SLA3
    AM_CONTROL_FUZZY.update_sla(
         to_date('2012-01-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), --p_i_window_start => 
         'SLA3: 10$ for every started second of an image processing longer by average than 10ms', --p_i_sla_resource_name => 
         'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]', --p_i_base_resource_like => 
         'ceil(10 * (count(*) * sum(r.metricvalue) / 1000))', --p_i_select_sla_value_phrase =>
         '1=1', --p_i_where_metric_phrase => -- no filter
         'avg(r.metricvalue) > 10'); --p_i_having_phrase => 
         
    -- run calculations
    ret := AM_CONTROL_FUZZY.prepare2(to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS'),
        'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]', 
        'CPU Combined:', 
        'SLA1: 1$ per every extra second over 2sec execution');
    
    ret := AM_CONTROL_FUZZY.eval_sla(
        to_date('2012-01-03 21:30:00', 'YYYY-MM-DD HH24:MI:SS'),
        'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]');
    dbms_output.put_line(ret);
end;

--- MAIN METHOD !!!! - does everything
begin
    --AM_CONTROL_FUZZY.run_evaluation(to_date('2012-01-03 22:30:00', 'YYYY-MM-DD HH24:MI:SS'));
    AM_CONTROL_FUZZY.run_evaluation(to_date('2012-01-16 14:30:00', 'YYYY-MM-DD HH24:MI:SS'));
end;

select * from temp_action order by 1;
select * from temp_res order by 1;
select * from temp_sla order by 1;

select * from temp_norm_15sec_aggs order by t;

select * from temp_fuzzy_rules order by setname, metricvalue;

--------------------------------------------------------------------

select * from am_raw_metric r where r.ts > sysdate - 1/24 and r.resourcename = 'CPU Combined:'
order by 1 asc;

select to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as t, sum(r.metricvalue), count(*)  
from am_raw_metric r where r.ts > sysdate - 3/24 
and r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
group by to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')
order by 1 ;

--------------------------------------------------------------------

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
            case when sate = 1 then sla + action_cnt * 2 else sla end as sla_new, -- set maximum value for termination
            case when sate = 1 then 0 else sla3 end as sla3_new -- quite optimistic, assuming all images will have resources to process fast 
    from temp_norm_15sec_aggs;
    
    
-- query SLA resources names 
-- all SLA
select r.resourcename, r.sourcename, count(*), sum(r.metricvalue)
from am_raw_metric r 
where r.resourcename like 'SLA%' -- is SLA
group by r.resourcename, r.sourcename order by 1;
-- SLA based on terminated action
select distinct r.resourcename
from am_raw_metric r 
where r.resourcename like 'SLA%' -- is SLA
and   r.sourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
-- SLA based on action which could have potential positive impacts
select distinct r.resourcename
from am_raw_metric r 
where r.resourcename like 'SLA%' -- is SLA
and   r.sourcename != 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
    
SLA1: 1$ per every extra second over 2sec execution
SLA2: 1$ extra for actions over 1sec execution
SLA3: 10$ for every started second of an image processing longer by average than 20ms
    

    select (trunc(to_date('2012-01-03 21:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'DD') - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 3600 
            - (to_date('2012-01-03 21:30:00', 'YYYY-MM-DD HH24:MI:SS') - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 3600 from dual;
-- calculate SLA
-- for terminated action SLA
         select sum(val) as sla_val, sum(sla_new) as sla_val_new
         from  (select sla.t, --trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
                       val, cnt,
                       case when agg.sate = 1 then 2 * agg.action_cnt else val end as sla_new, -- set maximum value for termination + TODO move cond to where
                       agg.*
                from (select trunc(p.x / 15) * 15 as t, sum(r.metricvalue) as val, count(case when r.metricvalue is not null then 1 end) as cnt
                      from (select r.metricvalue, r.ts, to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r
                            where  r.resourcename = 'SLA1: 1$ per every extra second over 2sec execution' -- p_i_sla_resource_name --
                            and    r.ts >= to_date('2012-01-03 21:30:00', 'YYYY-MM-DD HH24:MI:SS') -- p_i_window_start
                            and    r.ts <  1/24 + to_date('2012-01-03 21:30:00', 'YYYY-MM-DD HH24:MI:SS') -- p_i_window_start
                            ) r, am_pivot p
                      where r.sssss(+) = p.x
                      and   p.x >= 77400 --81000 --sec_at_window_start - sec_at_day_start 
                      and   p.x < 3600 + 77400 --81000 --sec_at_window_start - sec_at_day_start
                      group by trunc(p.x / 15) * 15
                      ) sla, 
                      temp_norm_15sec_aggs agg
                where sla.t = agg.t    
                order by sla.t
                );

-- for SLA not based on terminated action
         select sum(val) as sla_val, sum(sla_new) as sla_val_new
         from  (select sla.t, --trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
                       val, cnt,
                       case when agg.sate = 1 then 0 else val end as sla_new, -- set ZERO value for termination + TODO move cond to where
                       agg.*
                from (select trunc(p.x / 15) * 15 as t, sum(r.metricvalue) as val, count(case when r.metricvalue is not null then 1 end) as cnt
                      from (select r.metricvalue, r.ts, to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r
                            where  r.resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 10ms' -- p_i_sla_resource_name --
                            and    r.ts >= to_date('2012-01-03 22:30:00', 'YYYY-MM-DD HH24:MI:SS') -- p_i_window_start
                            and    r.ts <  1/24 + to_date('2012-01-03 22:30:00', 'YYYY-MM-DD HH24:MI:SS') -- p_i_window_start
                            ) r, am_pivot p
                      where r.sssss(+) = p.x
                      and   p.x >= 81000  --77400 --sec_at_window_start - sec_at_day_start 
                      and   p.x < 3600 + 81000  --77400 --sec_at_window_start - sec_at_day_start
                      group by trunc(p.x / 15) * 15
                      ) sla, 
                      temp_norm_15sec_aggs agg
                where sla.t = agg.t    
                order by sla.t
                );

---------------------------------------------------------------------------------------------------------------

select *from am_raw_metric

drop table temp_fuzzy_rules;



              

select * from temp_fuzzy_rules;

    update temp_norm_15sec_aggs set sate = null;
    update temp_norm_15sec_aggs 
    set    sate = 1
    where  res >=  -- sat_threshold
        (select min(res) as sat_threshold 
        from (select 5*round(res/5, 2) as res, 
                     avg(action) as action_avg, avg(sla) as sla_avg, 
                     --sum(action) as action_sum, sum(action_cnt) as action_cnt,
                     sum(avg(action)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(action)) over () as action_cumedist,
                     sum(avg(sla)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(sla)) over () as sla_cumedist
              from temp_norm_15sec_aggs 
              group by 5*round(res/5, 2) -- 1/5 -> 20 time buckets
              )
              where action_cumedist >= 0.1 --0.4 -- includes 60% of longest actions   
              and   sla_cumedist >= 0.0 -- includes 80% of higest SLA violations
        );
        
---

select * from am_raw_metric r where r.resourcename = 'CPU Combined:'
and r.ts > to_date('2012-01-05 13:15:00', 'YYYY-MM-DD HH24:MI:SS')
and r.ts < to_date('2012-01-05 14:15:00', 'YYYY-MM-DD HH24:MI:SS')
order by r.ts;
        
select sum (metricvalue) from (
  select * from am_raw_metric r 
  where r.resourcename = 'SLA1: 1$ per every extra second over 2sec execution'
  --where r.resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 10ms'
  and r.ts > to_date('2012-01-03 19:35:00', 'YYYY-MM-DD HH24:MI:SS')
  and r.ts < to_date('2012-01-03 19:45:00', 'YYYY-MM-DD HH24:MI:SS')
  --and r.ts > to_date('2012-01-08 17:50:00', 'YYYY-MM-DD HH24:MI:SS')
  --and r.ts < to_date('2012-01-08 18:00:00', 'YYYY-MM-DD HH24:MI:SS')
  order by r.ts
);


select count(*) cnt, sum(metricvalue)/ 1000 as sum, avg(metricvalue)/ 1000 as avg
select *
from am_raw_metric r 
where r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]'
--  and r.ts > to_date('2012-01-08 17:50:00', 'YYYY-MM-DD HH24:MI:SS')
--  and r.ts < to_date('2012-01-08 18:00:00', 'YYYY-MM-DD HH24:MI:SS')
and r.entrypoint = 'EXIT'
order by r.ts

select distinct rm.resourcename  from am_raw_metric rm

DiskQueue:
DiskReadBytes:
DiskReads:
DiskServiceTime:
DiskWriteBytes:
DiskWrites:

----------------------------------------------------------
-- analysing deep-history - using found segments of saturation, with level of 0.8

-- all values are normalized in this table
select * from temp_norm_15sec_aggs where res >= 0.8;

-- temp_... use not-normalized data
select * from temp_action where tss in (select t from temp_norm_15sec_aggs where res >= 0.8);
select * from temp_res where tss in (select t from temp_norm_15sec_aggs where res >= 0.8);
select * from temp_sla where tss in (select t from temp_norm_15sec_aggs where res >= 0.8);

-- trick to get all seconds inside 15 sec aggregate values in temp_...
select time + p.x/24/3600 from temp_res, am_pivot p where tss in (select t from temp_norm_15sec_aggs where res >= 0.8) and p.x < 15; -- 15 because temp has 15 secs aggregates

-- retrieve original data in found segments 
select * from am_raw_metric r 
where  r.ts in (select time + p.x/24/3600 from temp_res, am_pivot p where tss in (select t from temp_norm_15sec_aggs where res >= 0.8) and p.x < 15)
--and    r.resourcename = 'CPU Combined:'
and    r.resourcename = 'DiskQueue:'
--and    r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]' and r.entrypoint = 'EXIT'
--and    r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]' and r.entrypoint = 'EXIT'

-- generates histogram - 10 buckets - of data for found segmments (saturation times)
with s as (
     select /*+ materialize */ * from am_raw_metric r 
     where  r.ts in (select time + p.x/24/3600 from temp_res, am_pivot p where tss in (select t from temp_norm_15sec_aggs where res >= 0.8) and p.x < 15)
     --and    r.resourcename = 'CPU Combined:'
     and    r.resourcename = 'DiskQueue:' -- mostly very low
     --and    r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]' and r.entrypoint = 'EXIT'
     --and    r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]' and r.entrypoint = 'EXIT'
     ),
     smin as (select min(metricvalue) as m from s),
     smax as (select max(metricvalue) as m from s)
select --width_bucket(metricvalue, smin.m, smax.m, 10) as bucket_no, 
       width_bucket(metricvalue, smin.m, smax.m, 10) * (smax.m-smin.m)/10+smin.m as bucket, 
       count(*)
from s, smin, smax
group by --width_bucket(metricvalue, smin.m, smax.m, 10), 
         width_bucket(metricvalue, smin.m, smax.m, 10) * (smax.m-smin.m)/10+smin.m
order by 1;

-- we are searching deep-history data of most frequent values in found segments during last sliding-window
-- (potential performance problem - selectivity is high because we checkmost frequent situations)
select * from am_raw_metric r 
     where  r.resourcename = 'CPU Combined:'
     and    r.metricvalue between 0.8838 and 0.9414 -- richest bucket
     and    r.ts between (select min(time) - 1 from temp_res) and (select min(time) from temp_res) -- one day scope
;
select * from am_raw_metric r 
     where  r.resourcename = 'DiskQueue:'
     and    r.metricvalue between 2.7 and 5.4 -- richest bucket
     and    r.ts between (select min(time) - 1 from temp_res) and (select min(time) from temp_res) -- one day scope
;
select * from am_raw_metric r 
     where  r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]' and r.entrypoint = 'EXIT'
     and    r.metricvalue between 1940.7 and 1940.7 + 1940.7 -- richest bucket
;
