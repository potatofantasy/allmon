create or replace package AM_CONTROL_FUZZY is

  -- Author  : sikora.t
  -- Created : 2011-12-22 21:43:47
  -- Purpose : 
  
  -- Public type declarations
  --type <TypeName> is <Datatype>;
  
  -- Public constant declarations
  --<ConstantName> constant <Datatype> := <Value>;

  -- Public variable declarations
  --<VariableName> <Datatype>;

  -- Public function and procedure declarations
  --function <FunctionName>(<Parameter> <Datatype>) return <Datatype>;

  procedure update_sla(      p_i_window_start date,
                             p_i_sla_resource_name varchar2,
                             p_i_base_resource_like varchar2,
                             p_i_select_sla_value_phrase varchar2,
                             p_i_where_metric_phrase varchar2,
                             p_i_having_phrase varchar2, 
                             p_i_window_length_days number default 1/24);
  procedure populate_temp_tables(p_i_window_start date,
                                 p_i_action_name varchar2, 
                                 p_i_resource_name varchar2,
                                 p_i_sla_resource_name varchar2
                                 );
                                 
  function get_cdf_threshold( p_i_action_avg_cdf number,
                              --p_i_action_sum_cdf number,  
                              --p_i_action_cnt_cdf number,
                              p_i_sla_avg_cdf number ) return number;                                

  function  prepare_and_match_series(p_i_window_start date,
                                 p_i_action_name varchar2, 
                                 p_i_resource_name varchar2,
                                 p_i_sla_resource_name varchar2
                                 ) return number;
  function eval_sla(   p_i_window_start date,
                       p_i_termaction_resourcename varchar2) return number;
  
  -- main experiment 
  procedure run_evaluation( p_i_window_start date);
  
  procedure set_terminator_fuzzy_sets(p_i_monitored_resourcename varchar2);
  
end AM_CONTROL_FUZZY;
/
create or replace package body AM_CONTROL_FUZZY is

  -- Private type declarations
  --type <TypeName> is <Datatype>;
  
  -- Private constant declarations
  --<ConstantName> constant <Datatype> := <Value>;

  -- Private variable declarations
  --<VariableName> <Datatype>;
/*
  update_sla(
     'SLA3: 10$ for every started second of an image processing longer by average than 20ms', -- full descriptive name
     'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]',
     'ceil(100 * (count(*) * sum(r.metricvalue) / 1000))',
     '1=1',  --and  r.metricvalue > 1000 -- longer than 1 second
     'avg(r.metricvalue) > 10' -- --having avg(r.metricvalue) > 10 -- cummulative 1 second average aggregates over 10ms of execution
  );
*/

  -- time aggreagate is one second!
  procedure update_sla(      p_i_window_start date,
                             p_i_sla_resource_name varchar2,
                             p_i_base_resource_like varchar2,
                             p_i_select_sla_value_phrase varchar2,
                             p_i_where_metric_phrase varchar2,
                             p_i_having_phrase varchar2,
                             p_i_window_length_days number default 1/24) is
    insert_sql  varchar2(20000);
  begin
  
    delete from am_raw_metric 
    where resourcename = p_i_sla_resource_name --'SLA3: 10$ for every started second of an image processing longer by average than 20ms';
    and   ts >= p_i_window_start
    and   ts <  p_i_window_length_days + p_i_window_start;
    
    insert_sql := 
    'insert into am_raw_metric(am_rme_id, 
                              artifactcode, hostname, hostip, instancename, entrypoint, --threadname,
                              metrictypecode, resourcename, sourcename,
                              ts, metricvalue)
      select  am_rme_seq.nextval, 
              ''SYNTH'' as artifactcode, hostname, hostip, instancename, ''ENTRY'' as entrypoint, 
              ''ACTCLS-SLA'' as metrictypecode, '''||p_i_sla_resource_name||''' as resourcename, sourcename, 
              to_date(ts, ''YYYY-MM-DD HH24:MI:SS''), sla_value
      from (
        select hostname, hostip, instancename, 
               '''||p_i_base_resource_like||''' as sourcename,
               to_char(r.ts, ''YYYY-MM-DD HH24:MI:SS'') as ts,
               avg(r.metricvalue), sum(r.metricvalue), count(*),
               '||p_i_select_sla_value_phrase||' as sla_value 
        from  am_raw_metric r
        where r.resourcename like '''||p_i_base_resource_like||'''
        and  r.ts >= :1
        and  r.ts <  :2
        and   '||p_i_where_metric_phrase||'
        group by hostname, hostip, instancename, 
                 '''||p_i_base_resource_like||''',
                 to_char(r.ts, ''YYYY-MM-DD HH24:MI:SS'')
        having '||p_i_having_phrase||' 
        order by ts) sel';
    
    dbms_output.put_line('Main insert statement: '||insert_sql);
    
    execute immediate insert_sql 
    using p_i_window_start, 1/24 + p_i_window_start;
    
  end;


-- uses:
--create table TEMP_ACTION(
--  TSS  NUMBER,
--  TIME DATE,
--  SUM  NUMBER,
--  AVG  NUMBER,
--  CNT  NUMBER
--);
--create table TEMP_RES (
--  TSS      NUMBER,
--  TIME     DATE,
--  VAL      NUMBER,
--  LERP     NUMBER
--);
--create table TEMP_SLA (
--  TSS      NUMBER,
--  TIME     DATE,
--  VAL      NUMBER
--);
  -- one hour sliding window!!!
  procedure populate_temp_tables(p_i_window_start date,
                                 p_i_action_name varchar2, 
                                 p_i_resource_name varchar2,
                                 p_i_sla_resource_name varchar2
                                 ) is
    sec_at_day_start number;
    sec_at_window_start number;
  begin
  
    select (trunc(p_i_window_start, 'DD') - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 3600
    into sec_at_day_start from dual;
    select (p_i_window_start - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 3600
    into sec_at_window_start from dual;
    
    delete from temp_action;
    insert into temp_action (tss, time, sum, avg, cnt) 
      select p.x as tss, trunc(p_i_window_start, 'DD') + p.x / 24 / 3600 as time,
             sum, avg, cnt
      from (select to_char(r.ts, 'SSSSS') as sssss, --(r.ts - trunc(to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'DD')) * 24 * 3600 as sssss,
                   sum(r.metricvalue) as sum, avg(r.metricvalue) as avg, 
                   sum(case when r.metricvalue is null then 0 else 1 end) as cnt
            from  am_raw_metric r
            where r.resourcename = p_i_action_name --'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
            and   r.entrypoint = 'EXIT'
            and   r.ts >= p_i_window_start --to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') 
            and   r.ts <  1/24 + p_i_window_start --to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') + 1/24 --r.sssss > 504000 and r.sssss <= 504000 + 3600
            group by to_char(r.ts, 'SSSSS') --(r.ts - trunc(to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'DD')) * 24 * 3600
            ) r, am_pivot p
      where r.sssss(+) = p.x
      and   p.x >= sec_at_window_start - sec_at_day_start 
      and   p.x < 3600 + sec_at_window_start - sec_at_day_start
      order by p.x;
    
    delete from temp_res;
    insert into temp_res(tss, time, val, lerp)
      select s.tss, trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
             val, case when val is null then (y0 + (tss - x0) * (y1 - y0)/(x1 - x0)) else val end as lerp -- linear interpolation (http://en.wikipedia.org/wiki/Linear_interpolation)
      from (
            select s.*,
                   case when val is null then max(tss) over(order by aboveCnt range between 1 preceding and 1 preceding) else tss end as x0,
                   case when val is null then min(tss) over(order by belowCnt range between 1 preceding and 1 preceding) else tss end as x1,
                   case when val is null then max(Val) over(order by aboveCnt range between 1 preceding and 1 preceding) else val end as y0, --Lag1,
                   case when val is null then max(Val) over(order by belowCnt range between 1 preceding and 1 preceding) else val end as y1 --Lead1
            from (select s.*, 
                         count(val) over(order by tss rows between unbounded preceding and 1 preceding) aboveCnt,
                         count(val) over(order by tss rows between 1 following and unbounded following) belowCnt        
                  from (select p.x as tss, val
                        from (select to_char(r.ts, 'SSSSS') as sssss, avg(r.metricvalue) as val 
                              from   am_raw_metric r
                              where  r.resourcename = p_i_resource_name --'CPU Combined:'
                              and    r.ts >= p_i_window_start --to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS')
                              and    r.ts <  1/24 + p_i_window_start --to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS')
                              group by to_char(r.ts, 'SSSSS')
                              ) r, am_pivot p
                        where r.sssss(+) = p.x
                        and   p.x >= sec_at_window_start - sec_at_day_start 
                        and   p.x < 3600 + sec_at_window_start - sec_at_day_start
                        order by p.x
                ) s) s) s
      order by tss;
    
    delete from temp_sla;
    insert into temp_sla(tss, time, val) --, lerp)
      select s.tss, trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, val
      --select s.tss,  trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
      --       val, case when val is null then (y0 + (tss - x0) * (y1 - y0)/(x1 - x0)) else val end as lerp -- linear interpolation (http://en.wikipedia.org/wiki/Linear_interpolation)
      --from (
      --      select s.*,
      --             case when val is null then max(tss) over(order by aboveCnt range between 1 preceding and 1 preceding) else tss end as x0,
      --             case when val is null then min(tss) over(order by belowCnt range between 1 preceding and 1 preceding) else tss end as x1,
      --             case when val is null then max(Val) over(order by aboveCnt range between 1 preceding and 1 preceding) else val end as y0, --Lag1,
      --             case when val is null then max(Val) over(order by belowCnt range between 1 preceding and 1 preceding) else val end as y1 --Lead1
      --      from (select s.*, 
      --                   count(val) over(order by tss rows between unbounded preceding and 1 preceding) aboveCnt,
      --                   count(Val) over(order by tss rows between 1 following and unbounded following) belowCnt        
                from (
                      select to_char(r.ts, 'HH24:MI:SS') as time, avg(r.metricvalue) as val, p.x as tss --r.*, r.metricvalue as val, p.x as tss
                      from (select r.*, to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r
                            where  r.resourcename(+) = p_i_sla_resource_name -- 'SLA1: 1$ per every extra second over 2sec execution'
                            ) r, am_pivot p
                      where r.sssss(+) = p.x
                      and   p.x >= sec_at_window_start - sec_at_day_start 
                      and   p.x < 3600 + sec_at_window_start - sec_at_day_start
                      group by p.x, to_char(r.ts, 'HH24:MI:SS') 
                ) s
      --     ) s) s
      order by tss;
  
  end;


  -- base on cumulative distribution function (cdf) in resource function
  function get_cdf_threshold( p_i_action_avg_cdf number,
                              --p_i_action_sum_cdf number,  
                              --p_i_action_cnt_cdf number,
                              p_i_sla_avg_cdf number ) return number is 
    ret_threshold number;
  begin
  
    select min(res) as threshold 
    into ret_threshold
    from (select 5*round(res/5, 2) as res, 
                 avg(action) as action_avg, avg(sla) as sla_avg, 
                 --sum(action) as action_sum, sum(action_cnt) as action_cnt,
                 sum(avg(action)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(action)) over () as action_cumedist,
                 sum(avg(sla)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(sla)) over () as sla_cumedist
          from   temp_norm_15sec_aggs 
          group by 5*round(res/5, 2) -- 1/5 -> 20 time buckets
          )
          where action_cumedist >= p_i_action_avg_cdf --0.4 -- includes 60% of longest actions   
          and   sla_cumedist >= p_i_sla_avg_cdf -- includes 80% of higest SLA violations
    ;
    
    return ret_threshold;
  end;
  
  /*
  procedure getThreshold is
  TYPE EmpCurTyp IS REF CURSOR;
  emp_cv   EmpCurTyp;
   emp_rec  temp_matching_action%ROWTYPE;
   sql_stmt VARCHAR2(2000);
  begin
      
      --execute immediate 
      sql_stmt :=
      '
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
      order by 1 desc'; -- :1, :2, :3;;  USING dept_id, dept_name, location;
  
  
   --sql_stmt := 'SELECT * FROM emp WHERE job = :j';
   OPEN emp_cv FOR sql_stmt; -- USING my_job;
   LOOP
      FETCH emp_cv INTO emp_rec;
         DBMS_OUTPUT.PUT_LINE(emp_rec.t);
      EXIT WHEN emp_cv%NOTFOUND;
      -- process record
   END LOOP;
   CLOSE emp_cv;
   
--   OPEN emp_cv FOR sql_stmt;
--FOR REC IN emp_cv LOOP
--DBMS_OUTPUT.PUT_LINE(REC.t || REC.t);
--END LOOP;
--CLOSE emp_cv;
--EXCEPTION
--WHEN OTHERS THEN
--  DBMS_OUTPUT.PUT_LINE(SQLERRM);

  end;
*/   

    --normalized aggregates in 15sec buckets
--    drop table temp_norm_15sec_aggs; 
--    create table temp_norm_15sec_aggs(
--      t       number,
--      action  number,
--      action_cnt number,
--      res     number,
--      sla     number,
--      actione number, -- action segment event - based on moving average thresholding
--      rese    number, -- resource segment event - based on moving average thresholding
--      sate    number  -- saturation segment event - based on thresholdtaken form CDF analysis
--    );
    
  function prepare_and_match_series(p_i_window_start date,
                                 p_i_action_name varchar2, 
                                 p_i_resource_name varchar2,
                                 p_i_sla_resource_name varchar2
                                 ) return number is
    max_sum_sla number;
    max_sum_agg_sla number;
    max_sum_action number;
    max_sum_agg_action number;
    max_sum_res number;
    max_sum_agg_res number;

    action_res_dependency_coef number;
  begin  

    -- Parameters:
    -- sla
    -- action
    -- resource
    
    -- prepare temporary tables
    populate_temp_tables(p_i_window_start, p_i_action_name, p_i_resource_name, p_i_sla_resource_name);
    

    --Prepare data of a current position of the sliding window:
    select max(sum) into max_sum_action from temp_action;
    select max(s) into max_sum_agg_action from (select trunc(tss / 15) * 15, sum(sum) as s from temp_action group by trunc(tss / 15) * 15);
    select max(val) into max_sum_res from temp_res;
    select max(v) into max_sum_agg_res from (select trunc(tss / 15) * 15, avg(val) as v from temp_res group by trunc(tss / 15) * 15);
    select max(val) into max_sum_sla from temp_sla;
    select max(s) into max_sum_agg_sla from (select trunc(tss / 15) * 15, sum(val) as s from temp_sla group by trunc(tss / 15) * 15);

    -- populate aggregated ....
    delete from temp_norm_15sec_aggs;
    insert into temp_norm_15sec_aggs(t, action, action_cnt, res, sla) 
      select trunc(a.tss / 15) * 15 as t,
             case when sum(a.sum) is not null then sum(a.sum/max_sum_agg_action) else 0 end as action, 
             case when sum(a.cnt) is not null then sum(a.cnt) else 0 end as action_cnt, 
             case when avg(r.val) is not null then avg(r.val/max_sum_agg_res) end as res,
             case when sum(s.val) is not null then sum(s.val/max_sum_agg_sla) else 0 end as sla 
      from temp_action a, temp_res r, temp_sla s
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
        (select min(res) as sat_threshold 
        from (select 5*round(res/5, 2) as res, 
                     avg(action) as action_avg, avg(sla) as sla_avg, 
                     --sum(action) as action_sum, sum(action_cnt) as action_cnt,
                     sum(avg(action)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(action)) over () as action_cumedist,
                     sum(avg(sla)) over (order by 5*round(res/5, 2) range unbounded preceding) / sum(avg(sla)) over () as sla_cumedist
              from temp_norm_15sec_aggs 
              group by 5*round(res/5, 2) -- 1/5 -> 20 time buckets
              )
              where action_cumedist >= 0.3 --0.4 -- includes 60% of longest actions   
              and   sla_cumedist >= 0.2 -- includes 80% of higest SLA violations
        );
    
    commit;
                  
    -- C2: Calculate Dependency Coefficient as a product of Matches Strength and Overlapping: 
    -- Segments strength: S = 1 - (1 / (1 + CNT_RE * CNT_AE)) - .... emphasizes strength of found sets as measure of their quantity; lim(cntre->inf, cntae->inf) = 1
    -- TODO 
    -- Overlapping = P(RE and AE) / P(RE or AE) * P(SAT and RE and AE) / P(RE and AE); first item emphasizes quality of resources vs actions ?what if SLA base on res? overlapping and second found saturation threshold precision.
    select count(actionandrese) / count(actionorrese) * count(satandactionandrese) / count(actionandrese) as O
           --count(actionandrese) / count(actionorrese), count(satandactionandrese) / count(actionandrese)
    into   action_res_dependency_coef
    from (select t, action, res, sla,
                 case when actione is not null and rese is not null then 1 end as actionandrese, 
                 case when actione is not null or rese is not null then 1 end as actionorrese,
                 case when sate = 1 and actione = 1 and rese = 1 then 1 end as satandactionandrese
                 -- matching between res, action and SLA is not necessary, this evaluation will be done in SLA eval step
          from temp_norm_15sec_aggs);

    if action_res_dependency_coef < 0.2 then 
       return -1; -- resource vs action dependency coefficient is too low
    end if;
    
    --  SLA eval - calculate sla pros & cons of the potential control
--    select  t, action, action_cnt, cpu, dqueue, sla, sla3, sla + sla3 as sla_sum, actione1, cpue1, actioncpue1, actionorcpue1,
--            cpusat, cpusatrule,
--            case when cpusatrule is not null then sla + action_cnt/50 * 2 else sla end as sla_new,
--            case when cpusatrule is not null then 0 else sla3 end as sla3_new -- quite optimistic, assuming all images will have resources to process fast 
--    from temp_norm_15sec_aggs;

    

    return 1; 
  end;


  function eval_sla(   p_i_window_start date,
                       p_i_termaction_resourcename varchar2) return number is
    termaction_sum_sla_val number;
    termaction_sum_sla_val_new number;
    other_sum_sla_val number;
    other_sum_sla_val_new number;

    sec_at_day_start number;
    sec_at_window_start number;
  begin
  
    select (trunc(p_i_window_start, 'DD') - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 3600
    into sec_at_day_start from dual;
    select (p_i_window_start - to_date('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) * 24 * 3600
    into sec_at_window_start from dual;
      
    -- query SLA resources names 
    -- all SLA
    --select distinct r.resourcename, r.sourcename 
    --from am_raw_metric r 
    --where r.resourcename like 'SLA%' -- is SLA
/*    
    -- SLA based on terminated action
    select distinct r.resourcename
    from  am_raw_metric r 
    where r.artifactcode = 'SYNTH' and r.metrictypecode = 'ACTCLS-SLA' --r.resourcename like 'SLA%' -- is SLA
    and   r.sourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'; -- p_i_termaction_resourcename
    -- SLA based on action which could have potential positive impacts
    select distinct r.resourcename
    from am_raw_metric r 
    where r.resourcename like 'SLA%' -- is SLA
    and   r.sourcename != 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'; -- p_i_termaction_resourcename
*/        
    --SLA1: 1$ per every extra second over 2sec execution
    --SLA2: 1$ extra for actions over 1sec execution
    --SLA3: 10$ for every started second of an image processing longer by average than 20ms

-- TODO performance -> move "when agg.sate = 1 then" cond to where clause      

-- calculate SLA
-- for terminated action SLA
         select sum(val) as sla_val, sum(sla_new) as sla_val_new
         into   termaction_sum_sla_val, termaction_sum_sla_val_new
         from  (select sla.t, --trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
                       val, cnt,
                       case when agg.sate = 1 then 2 * agg.action_cnt else val end as sla_new, -- set maximum value for termination
                       agg.*
                from (select trunc(p.x / 15) * 15 as t, sum(r.metricvalue) as val, count(case when r.metricvalue is not null then 1 end) as cnt
                      from (select r.metricvalue, r.ts, to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r
                            where  r.resourcename = 'SLA1: 1$ per every extra second over 2sec execution' -- p_i_sla_resource_name --
                            and    r.ts >= p_i_window_start -- p_i_window_start
                            and    r.ts <  1/24 + p_i_window_start -- p_i_window_start
                            ) r, am_pivot p
                      where r.sssss(+) = p.x
                      and   p.x >= sec_at_window_start - sec_at_day_start 
                      and   p.x < 3600 + sec_at_window_start - sec_at_day_start 
                      group by trunc(p.x / 15) * 15
                      ) sla, 
                      temp_norm_15sec_aggs agg
                where sla.t = agg.t    
                order by sla.t
                );

-- TODO check all other SLA
-- for SLA not based on terminated action
         select sum(val) as sla_val, sum(sla_new) as sla_val_new
         into   other_sum_sla_val, other_sum_sla_val_new
         from  (select sla.t, --trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
                       val, cnt,
                       case when agg.sate = 1 then 0 else val end as sla_new, -- set ZERO value for termination + TODO move cond to where
                       agg.*
                from (select trunc(p.x / 15) * 15 as t, sum(r.metricvalue) as val, count(case when r.metricvalue is not null then 1 end) as cnt
                      from (select r.metricvalue, r.ts, to_char(r.ts, 'SSSSS') as sssss from am_raw_metric r
                            where  r.resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 10ms' -- p_i_sla_resource_name --
                            and    r.ts >= p_i_window_start -- p_i_window_start
                            and    r.ts <  1/24 + p_i_window_start -- p_i_window_start
                            ) r, am_pivot p
                      where r.sssss(+) = p.x
                      and   p.x >= sec_at_window_start - sec_at_day_start 
                      and   p.x < 3600 + sec_at_window_start - sec_at_day_start 
                      group by trunc(p.x / 15) * 15
                      ) sla, 
                      temp_norm_15sec_aggs agg
                where sla.t = agg.t    
                order by sla.t
                );
  
    -- current SLA minus new projected SLA values - if result is greater than 0 then 
    --   this makes sense to terminate action set as parameter in times given in temp_norm_15sec_aggs.sate
    return (termaction_sum_sla_val + other_sum_sla_val) - (termaction_sum_sla_val_new + other_sum_sla_val_new);
  
  end;


  procedure run_evaluation(  p_i_window_start date) is
    p_i_mon_action_resourcename am_raw_metric.resourcename%type;
    p_i_mon_4contr_resourcename am_raw_metric.resourcename%type;
    
    matching_res number;
    sla_eval_res number;
  begin 
      -- update SLA1
      AM_CONTROL_FUZZY.update_sla(
           p_i_window_start,
           'SLA4: 1$ per every extra second over 2sec execution',
           'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]',
           --'(sum(r.metricvalue) - 2000) / 1000',
           ' (case when (sum(metricvalue) - 2000) / 1000 < 5 then (sum(metricvalue) - 2000) / 1000 else 5 end) ', -- but no more than 5$ penalty
           'metricvalue > 2000', -- actions longer than 2 secs
           '1=1');

      -- update SLA3
      AM_CONTROL_FUZZY.update_sla(
           p_i_window_start,
           'SLA3: 10$ for every started second of an image processing longer by average than 10ms',
           'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]',
           'ceil(10 * (count(*) * sum(r.metricvalue) / 1000))',
           '1=1', --'r.metricvalue > 1000'
           'avg(r.metricvalue) > 10');

      -- find maximum SLA, get source Action and iterate through resources selecting the best fitting resource signal,
      -- which can be later used by Controller to decide about control action

      p_i_mon_action_resourcename := 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]';
      p_i_mon_4contr_resourcename := 'CPU Combined:'; --'DiskQueue:'; 

      -- run calculations
      matching_res := AM_CONTROL_FUZZY.prepare_and_match_series(
          p_i_window_start, -- to_date('2012-01-03 19:00:00', 'YYYY-MM-DD HH24:MI:SS'),
          p_i_mon_action_resourcename, 
          p_i_mon_4contr_resourcename, 
          'SLA4: 1$ per every extra second over 2sec execution');
      dbms_output.put_line('Action and Resources Matching process result: ' || matching_res);

      sla_eval_res := AM_CONTROL_FUZZY.eval_sla(p_i_window_start, p_i_mon_action_resourcename);
      dbms_output.put_line('SLA evaluation result: ' || sla_eval_res);

      if matching_res > 0 and sla_eval_res > 0 then 
         set_terminator_fuzzy_sets(p_i_mon_4contr_resourcename);
      end if;

  end;

-- uses:
/*
create table temp_fuzzy_rules(
  actuatortypename  varchar2(100),
  resourcename      varchar2(1000), 
  metricvalue       number(13,3),
  setname           VARCHAR2(100),
  membership        number --<0; 1> --strenght 
);*/
  procedure set_terminator_fuzzy_sets(p_i_monitored_resourcename varchar2) is
  begin 
    -- use of CDF:
    --am_control_fuzzy.get_cdf_threshold( 0.4, -- p_i_action_avg_cdf --0.4 -- includes 60% of avg longest actions   
    --                                    0.2  -- p_i_sla_avg_cdf -- includes 80% of higest avg SLA violations
    --                                  ), 
    delete from temp_fuzzy_rules where actuatortypename = 'TERMINATOR' and resourcename = p_i_monitored_resourcename;
    -- SAT - saturation
    insert into temp_fuzzy_rules(actuatortypename, resourcename, setname, metricvalue, membership)
    select 'TERMINATOR', p_i_monitored_resourcename, 'SAT', 0, 0 from dual
    union all
    select 'TERMINATOR', p_i_monitored_resourcename, 'SAT', am_control_fuzzy.get_cdf_threshold(0.2, 0.1), 0 from dual 
    union all
    select 'TERMINATOR', p_i_monitored_resourcename, 'SAT', am_control_fuzzy.get_cdf_threshold(0.4, 0.2), 1 from dual
    union all
    select 'TERMINATOR', p_i_monitored_resourcename, 'SAT', 1, 1 from dual;
    -- OK -- normal state - no control actions
    insert into temp_fuzzy_rules(actuatortypename, resourcename, setname, metricvalue, membership)
    select 'TERMINATOR', p_i_monitored_resourcename, 'OK', 0, 1 from dual
    union all
    select 'TERMINATOR', p_i_monitored_resourcename, 'OK', am_control_fuzzy.get_cdf_threshold(0.2, 0.1), 1 from dual
    union all
    select 'TERMINATOR', p_i_monitored_resourcename, 'OK', am_control_fuzzy.get_cdf_threshold(0.4, 0.2), 0 from dual
    union all
    select 'TERMINATOR', p_i_monitored_resourcename, 'OK', 1, 0 from dual;  
  
    commit;
  end;

--begin
  -- Initialization
  --<Statement>;
end AM_CONTROL_FUZZY;
/
