
begin 
  AM_CONTROL_FUZZY.prepare2;
end;

select * from temp_norm_15sec_aggs;

---------------------------------------------------------------
--normalized aggregates in 15sec buckets
drop table temp_norm_15sec_aggs; 
create table temp_norm_15sec_aggs(
  t       number,
  action  number,
  res     number,
  sla     number,
  actione number, -- action segment event - based on moving average thresholding
  rese    number, -- resource segment event - based on moving average thresholding
  sate    number  -- saturation segment event - based on thresholdtaken form CDF analysis
);


---------------------------------------------------------------
-- Parameters:
-- sla
-- action
-- resource

--Prepare data of a current position of the sliding window:
select max(sum) from temp_sla;
select max(s) from (select trunc(tss / 15) * 15, sum(sum) as s from temp_sla group by trunc(tss / 15) * 15);
select max(sum) from temp_action;
select max(s) from (select trunc(tss / 15) * 15, sum(sum) as s from temp_action group by trunc(tss / 15) * 15);
select max(val) from temp_cpu;
select max(v) from (select trunc(tss / 15) * 15, avg(val) as v from temp_cpu group by trunc(tss / 15) * 15);


delete from temp_norm_15sec_aggs;
insert into temp_norm_15sec_aggs(t, action, res, sla) 
select trunc(a.tss / 15) * 15 as t,
       case when sum(a.sum) is not null then sum(a.sum/57782) else 0 end as action, 
       --case when sum(a.cnt) is not null then sum(50*a.cnt) else 0 end as action_cnt, 
       case when avg(r.val) is not null then avg(r.val/0.919666666666667) end as res,
       case when sum(s.sum) is not null then sum(s.sum/33.76) else 0 end as sla 
from temp_action a, temp_cpu r, temp_sla s
where a.tss = r.tss and a.tss = s.tss
--and   a.tss < 504360
group by trunc(a.tss / 15) * 15;

-- Bxx: selecting segments for matching process and updating aggregates strucutre
update temp_norm_15sec_aggs ag
set (actione, rese) = (
        select --t, action, /*action_cnt,*/ res, sla, --mavga, mavgc, mminc, cpu - mminc,
               case when action > 1.2*mavga then 1 end as actione, -- actrion events 
               case when res - mminr > 1.2*mavgr then 1 end as rese -- resource events
        from (
            select t, action, --action_cnt, 
                   res, sla,
                   (select avg(action) from temp_norm_15sec_aggs a1 where a1.t between sel.t - 5*60 and sel.t + 5*60) as mavga, -- moving avg action time in 5mins neighberhood
                   (select avg(r1.res) from temp_norm_15sec_aggs r1 where r1.t between t - 5*60 and t + 5*60) as mavgr,
                   (select min(r1.res) from temp_norm_15sec_aggs r1 where r1.t between t - 5*60 and t + 5*60) as mminr, -- moving min resource
                   (select avg(s1.sla) from temp_norm_15sec_aggs s1 where s1.t between t - 5*60 and t + 5*60) as mavgs
            from temp_norm_15sec_aggs sel)
        where t = ag.t);

-- C1: Calculate termination thresholds for the best matching resource - from CDF of Action distribution in resources utilization function.  
/*select res, action_avg, sla_avg, action_cumedist,
       case when action_cumedist < 0.6 then 1 when action_cumedist < 0.8 then 1/(action_cumedist/0.2) else 0 end as ok, -- fuzzy set scopes
       case when action_cumedist > 0.8 then 1 when action_cumedist > 0.6 then 1/(action_cumedist/0.2) else 0 end as sat
from (
    select 5*round(res/5, 2) as res, avg(action) as action_avg, avg(sla) as sla_avg,
           sum(avg(action)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(action)) over () as action_cumedist
    from temp_norm_15sec_aggs 
    group by 5*round(res/5, 2) -- 1/5 -> 20 buckets
)    
order by 1;
*/

-- updating saturation threshold process
update temp_norm_15sec_aggs set sate = null;
update temp_norm_15sec_aggs 
set    sate = 1
where  res >=  -- sat_threshold
    (select min(res) as sat_threshold from (
        select 5*round(res/5, 2) as res, avg(action) as action_avg, avg(sla) as sla_avg,
               sum(avg(action)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(action)) over () as action_cumedist,
               sum(avg(sla)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(sla)) over () as sla_cumedist
        from temp_norm_15sec_aggs 
        group by 5*round(res/5, 2) -- 1/5 -> 20 buckets
    )
    where action_cumedist >= 0.4 -- includes 60% of longest actions   
    and   sla_cumedist >= 0.2 -- includes 80% of higest SLA violations
    );
              
-- C2: Calculate Dependency Coefficient as a product of Matches Strength and Overlapping: 
-- Segments strength: S = 1 - (1 / (1 + CNT_RE * CNT_AE)) - .... emphasizes strength of found sets as measure of their quantity; lim(cntre->inf, cntae->inf) = 1
-- TODO 
-- Overlapping = P(RE and AE) / P(RE or AE) * P(SAT and RE and AE) / P(RE and AE); first item emphasizes quality of resources vs actions ?what if SLA base on res? overlapping and second found saturation threshold precision.
select count(actionandrese) / count(actionorrese) * count(satandactionandrese) / count(actionandrese) as O,
       count(actionandrese) / count(actionorrese), count(satandactionandrese) / count(actionandrese)
from (
    select t, action, res, sla,
           case when actione is not null and rese is not null then 1 end as actionandrese, 
           case when actione is not null or rese is not null then 1 end as actionorrese,
           case when sate = 1 and actione = 1 and rese = 1 then 1 end as satandactionandrese
    from temp_norm_15sec_aggs);




-- calcualte sla pros & cons of the potential control
select  t, action, action_cnt, cpu, dqueue, sla, sla3, sla + sla3 as sla_sum, actione1, cpue1, actioncpue1, actionorcpue1,
        cpusat, cpusatrule,
        case when cpusatrule is not null then sla + action_cnt/50 * 2 else sla end as sla_new,
        case when cpusatrule is not null then 0 else sla3 end as sla3_new -- quite optimistic, assuming all images will have resources to process fast 
from temp_norm_15sec_aggs




--------------- original complex query ------------------------------

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
