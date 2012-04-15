

begin 
  am_control_fuzzy_hist.refresh_dimensions;
end;

begin 
  am_control_fuzzy_hist.load_dimensions(
                                's%', -- all resources
                                p_i_data_filter_start => to_date('2011-12-05', 'YYYY-MM-DD HH24'),
                                p_i_data_filter_end => to_date('2011-12-07', 'YYYY-MM-DD HH24:MI'));
end;

begin 
  am_control_fuzzy_hist.load_dimension(
                                'r130', --'r137',
                                p_i_data_filter_start => to_date('2011-12-05 13', 'YYYY-MM-DD HH24'),
                                p_i_data_filter_end => to_date('2011-12-05 15:30', 'YYYY-MM-DD HH24:MI'));
end;

begin 
  am_control_fuzzy_hist.load_dimension(
                                'r128', --'r130', --'r137',
                                p_i_data_filter_start => to_date('2011-12-05', 'YYYY-MM-DD HH24'),
                                p_i_data_filter_end => to_date('2011-12-07', 'YYYY-MM-DD HH24:MI'));
end;

-----------------------------------------------------------------------------------------------------

select (to_date('2011-12-08 17', 'YYYY-MM-DD HH24') - to_date('2011-12-05 13', 'YYYY-MM-DD HH24')) * 24 from dual;


select * from am_all_dims_norm_aggs_meta;

select * from am_all_dims_norm_aggs where a121 is not null order by 1;

select * from am_all_dims_norm_aggs a
where  a.ts > to_date('2011-12-05 00', 'YYYY-MM-DD HH24')
and    a.ts < to_date('2011-12-07 00', 'YYYY-MM-DD HH24') order by 1;


  drop table am_temp_resource;
  create global temporary table am_temp_resource (
      --ts   date not null,
      yyyy number not null,
      mm   number not null,
      dd   number not null,
      hh   number not null,
      mi   number not null,
      --ss   number not null,
      tss  number not null,
      r_avg number,
      a_sum number, a_cnt number );
  alter table am_temp_resource add constraint am_tr_pk primary key (yyyy, mm, dd, hh, mi, tss) using index;


select * from am_temp_resource;

          insert into am_temp_resource(yyyy, mm, dd, hh, mi, tss, a_sum, a_cnt)
              select to_char(ts, 'YYYY') as yyyy, to_char(ts, 'MM') as mm, to_char(r.ts, 'DD') as dd, to_char(r.ts, 'HH24') as hh, to_char(r.ts, 'MI') as mi,
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15 as tss, 
                     sum(r.metricvalue), count(1)
              from   am_raw_metric r, am_all_dims_norm_aggs_meta m
              where  r.artifactcode = m.artifactcode and r.metrictypecode = m.metrictypecode and r.resourcename = m.resourcename
              and    m.columnname = 'a121'--p_i_dim_columnname --'r131'
              and    r.ts >=  to_date('2011-12-05 00', 'YYYY-MM-DD HH24') --p_i_data_filter_start --
              and    r.ts <   to_date('2011-12-06 00', 'YYYY-MM-DD HH24') --p_i_data_filter_end --
              ----and    r.ts > max_ts in agg tab
              ----and    r.ts between a.ts and a.ts + 1/24/60/4 
              group by 
                     to_char(ts, 'YYYY'), to_char(ts, 'MM'), to_char(r.ts, 'DD'), to_char(r.ts, 'HH24'), to_char(r.ts, 'MI'),
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15;

      update (select a.a121 as d_sum, t.a_sum --a.a121sum as a_sum, a.a121cnt as a_cnt, t.r --a.'||p_i_dim_columnname||' =
              from   am_all_dims_norm_aggs a, am_temp_resource t
              where  a.yyyy = t.yyyy and a.mm = t.mm and a.dd = t.dd and a.hh = t.hh and a.mi = t.mi and a.tss = t.tss
              and    a.ts >=  to_date('2011-12-05 00', 'YYYY-MM-DD HH24')
              and    a.ts <   to_date('2011-12-06 01', 'YYYY-MM-DD HH24'))
      set d_sum = a_sum 




      update 
             (select a.r131 as d, --a.'||p_i_dim_columnname||' =
                     to_char(r.ts, 'YYYY') as yyyy, to_char(r.ts, 'MM') as mm, to_char(r.ts, 'DD') as dd, to_char(r.ts, 'HH24') as hh, to_char(r.ts, 'MI') as mi,
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15 as tss, 
                     avg(r.metricvalue) as r
              from   am_raw_metric r, am_all_dims_norm_aggs_meta m, am_all_dims_norm_aggs a
              where  r.artifactcode = m.artifactcode and r.metrictypecode = m.metrictypecode and r.resourcename = m.resourcename
              and    m.columnname = 'r131'
              --where  r.artifactcode = ''OS'' and r.metrictypecode = ''OSIO'' and r.resourcename = ''DiskQueue:''
              and    r.ts >=  to_date('2011-12-05 21', 'YYYY-MM-DD HH24')
              and    r.ts <   to_date('2011-12-05 22', 'YYYY-MM-DD HH24')
              and    a.yyyy = to_char(r.ts, 'YYYY') and a.mm = to_char(r.ts, 'MM') and a.dd = to_char(r.ts, 'DD') and a.hh = to_char(r.ts, 'HH24') and a.mi = to_char(r.ts, 'MI') 
              and    a.tss = trunc(to_char(r.ts, 'SSSSS') / 15) * 15
              ----and    r.ts > max_ts in agg tab
              ----and    r.ts between a.ts and a.ts + 1/24/60/4 
              group by a.r131,
                     to_char(r.ts, 'YYYY'), to_char(r.ts, 'MM'), to_char(r.ts, 'DD'), to_char(r.ts, 'HH24'), to_char(r.ts, 'MI'),
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15)
      set d = r 

         
      where  a.ts >=  to_date('2011-12-06 00', 'YYYY-MM-DD HH24')
      and    a.ts <   to_date('2011-12-06 01', 'YYYY-MM-DD HH24')




-- resource 
    insert into temp_res(tss, time, val, lerp)
      select s.tss, --trunc(p_i_window_start, 'DD') + s.tss / 24 / 3600 as time, 
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
                              where  r.resourcename = 'CPU Combined:' -- p_i_resource_name --
                              and    r.ts >= to_date('2011-12-04 22:00:00', 'YYYY-MM-DD HH24:MI:SS') --p_i_window_start --
                              and    r.ts <  to_date('2011-12-04 23:00:00', 'YYYY-MM-DD HH24:MI:SS') --1/24 + p_i_window_start --
                              group by to_char(r.ts, 'SSSSS')
                              ) r, am_pivot p
                        where r.sssss(+) = p.x
                        and   p.x >= 79204 --sec_at_window_start - sec_at_day_start 
                        and   p.x < 3600 + 79204  --sec_at_window_start - sec_at_day_start
                        order by p.x
                ) s) s) s
      order by tss;
    
-- action
    insert into temp_action (tss, time, sum, avg, cnt) 
      select p.x as tss, trunc(p_i_window_start, 'DD') + p.x / 24 / 3600 as time,
             sum, avg, cnt
      from (select to_char(r.ts, 'SSSSS') as sssss, --(r.ts - trunc(to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'DD')) * 24 * 3600 as sssss,
                   sum(r.metricvalue) as sum, avg(r.metricvalue) as avg, 
                   sum(case when r.metricvalue is null then 0 else 1 end) as cnt
            from  am_raw_metric r
            where r.resourcename = p_i_action_name --'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]'
            and   r.entrypoint = 'EXIT'
            and   r.ts >= to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') --p_i_window_start --
            and   r.ts <  to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS') + 1/24 --r.sssss > 504000 and r.sssss <= 504000 + 3600 -- 1/24 + p_i_window_start --
            group by to_char(r.ts, 'SSSSS') --(r.ts - trunc(to_date('2011-12-05 20:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'DD')) * 24 * 3600
            ) r, am_pivot p
      where r.sssss(+) = p.x
      and   p.x >= sec_at_window_start - sec_at_day_start 
      and   p.x < 3600 + sec_at_window_start - sec_at_day_start
      order by p.x;
    

