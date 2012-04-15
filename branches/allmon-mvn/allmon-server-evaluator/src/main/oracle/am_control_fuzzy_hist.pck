create or replace package am_control_fuzzy_hist is

  -- Author  : sikora.t
  -- Created : 2011-02-15 21:43:47
  -- Purpose : 
  

  
  procedure refresh_dimensions;
  
  procedure load_dimensions(  p_i_likephrase_dim_columnname varchar2,
                              p_i_data_filter_start date, 
                              p_i_data_filter_end date
                            );
  procedure load_dimension(  p_i_dim_columnname varchar2,
                             p_i_data_filter_start date, 
                             p_i_data_filter_end date
                           );
  procedure load_dimension_hh(  p_i_dim_columnname varchar2,
                                p_i_data_filter_start date
                              );
  
  -- helper
  function dim_column_name( p_i_artifactcode varchar2,
                            p_i_am_ald_id number) return varchar2;

  
end am_control_fuzzy_hist;
/
create or replace package body am_control_fuzzy_hist is

  /** 
  -- tables needed for the package - to aggregate data and store in a multi-dim structure
  drop table am_all_dims_aggs;
  create table am_all_dims_aggs (
    ts   date not null,
    yyyy number not null,
    mm   number not null,
    dd   number not null,
    hh   number not null,
    mi   number not null,
    --ss   number not null,
    tss  number not null
    -- columns added...
  );
  drop table am_all_dims_norm_aggs;
  create table am_all_dims_norm_aggs (
    ts   date not null,
    yyyy number not null,
    mm   number not null,
    dd   number not null,
    hh   number not null,
    mi   number not null,
    --ss   number not null,
    tss  number not null
    -- columns added...
  );
  alter table am_all_dims_norm_aggs add constraint am_alm_pk primary key (yyyy, mm, dd, hh, mi, tss);
  --create bitmap index am_ald_yy_idx on AM_ALL_DIMS_NORM_AGGS (yyyy);
  --create bitmap index am_ald_mm_idx on AM_ALL_DIMS_NORM_AGGS (mm);
  --create bitmap index am_ald_dd_idx on AM_ALL_DIMS_NORM_AGGS (dd);
  --create bitmap index am_ald_mi_idx on AM_ALL_DIMS_NORM_AGGS (mi);
  create unique index am_ald_cal_idxu on am_all_dims_norm_aggs (yyyy, mm, dd, mi, tss); 
  create index am_ald_cal_idx on am_all_dims_norm_aggs (yyyy, mm, dd, mi);
  create index am_ald_tss_idx on am_all_dims_norm_aggs (tss);
  --
  drop table am_all_dims_norm_aggs_meta;
  create table am_all_dims_norm_aggs_meta (
    am_ald_id number,
    columnname varchar2(10),
    artifactcode varchar2(20), 
    metrictypecode varchar2(20), 
    resourcename varchar2(500),
    min number,
    max number, 
    max_ts date
  );
  create sequence am_ald_seq;
  -- temp
  drop table temp_resource;
  create global temporary table am_temp_resource (
      --ts   date not null,
      yyyy number not null,
      mm   number not null,
      dd   number not null,
      hh   number not null,
      mi   number not null,
      --ss   number not null,
      tss  number not null,
      r    number );
  alter table am_temp_resource add constraint am_tr_pk primary key (yyyy, mm, dd, hh, mi, tss) using index;
  */

  -----------------------------------------------------------------------------
  -- a. scans ram metrics table artifacts, metric types, resources, etc used
  -- b. updates metadata (long operation, because all raw table data are taken)
  -- c. creates columns - alter data table, foreach dimension which has no column in a data table 
  procedure refresh_dimensions is 
      cursor cur_dim is
        select m.am_ald_id, m.artifactcode, m.metrictypecode 
        from am_all_dims_norm_aggs_meta m
        where m.columnname is null order by 1;
      rec_dim cur_dim%rowtype;
      columnname am_all_dims_norm_aggs_meta.columnname%type;
      columncount number;
  begin
      -- updates metadata (long operation, because all raw table data are taken)
      --/*
      dbms_output.put_line('Searching dimensions in raw metrics data, and updating meta-data table...');
      insert into am_all_dims_norm_aggs_meta(am_ald_id, artifactcode, metrictypecode, resourcename, min, max, max_ts)
          select am_ald_seq.nextval, artifactcode, r.metrictypecode, r.resourcename, 0, 0, null
          from (select distinct r.artifactcode, r.metrictypecode, r.resourcename --r.hostname, r.hostip, r.instancename, 
                from   am_raw_metric r
                where  (r.artifactcode, r.metrictypecode, r.resourcename) not in (select artifactcode, metrictypecode, resourcename from am_all_dims_norm_aggs_meta)
                --where  r.artifactcode||'://'||r.metrictypecode||'://'||r.resourcename not in (select artifactcode||'://'||metrictypecode||'://'||resourcename from am_all_dims_norm_aggs_meta)
                and    r.ts >= (select nvl(min(max_ts), sysdate-(366*100)) from am_all_dims_norm_aggs_meta)
                order by 1, 2, 3) r;
      commit;
      --*/
      
      -- creates columns - alter data table
      --foreach dimension which has no column in a data table 
      open cur_dim; --open the cursor to iterate it
      loop
          fetch cur_dim into rec_dim; --fetch a row data from the cursor and store it in rec_example
          exit when cur_dim%notfound; --if there is no more data in the cursor then exit
          --output the values of the cursor to the console note the use of . to access the fields of the record
          dbms_output.put_line('>>' || rec_dim.am_ald_id || '>' || rec_dim.artifactcode || '>' || rec_dim.metrictypecode);
          columnname := dim_column_name(rec_dim.artifactcode, rec_dim.am_ald_id);
          dbms_output.put('Checking column: '||columnname||'... ');
          select count(1) into columncount from cols c where c.table_name = upper('am_all_dims_norm_aggs') and c.column_name = upper(columnname);
          if columncount = 0 then 
              dbms_output.put('Adding column ...'||columnname);
              execute immediate 'alter table am_all_dims_norm_aggs add '||columnname||' number'; --execute immediate 'alter table am_all_dims_norm_aggs add :1 number' using columnname;
              execute immediate 'create index am_ald_'||columnname||'_idx on am_all_dims_norm_aggs ('||columnname||')'; --execute immediate 'create index am_ald_r1_idx on am_all_dims_norm_aggs (:1)' using columnname;
              --update am_all_dims_norm_aggs_meta m set m.columnname = columnname where m.am_ald_id = rec_dim.am_ald_id;
              --commit;
              dbms_output.put_line('Column added!');
          else 
              dbms_output.put_line('Column already exists!');
          end if;
      end loop;
      close cur_dim; --close the cursor

      -- update column names     
      update am_all_dims_norm_aggs_meta m 
      set m.columnname = dim_column_name(m.artifactcode, m.am_ald_id);
      commit;

      --exception when others then dbms_output.put_line(sqlerrm);
    
  end;

  -----------------------------------------------------------------------------
  procedure load_dimensions(  p_i_likephrase_dim_columnname varchar2,
                              p_i_data_filter_start date, 
                              p_i_data_filter_end date
                            ) is
      cursor cur_dim is
          select m.am_ald_id, m.artifactcode, m.metrictypecode, m.columnname
          from am_all_dims_norm_aggs_meta m
          where m.columnname is not null and m.columnname like p_i_likephrase_dim_columnname order by 1;
      rec_dim cur_dim%rowtype;
  begin 
      dbms_output.put_line('Calculating aggregates for each of dimensions like '||p_i_likephrase_dim_columnname);
      --foreach dimension which has no column in a data table 
      open cur_dim; --open the cursor to iterate it
      loop
          fetch cur_dim into rec_dim; --fetch a row data from the cursor and store it in rec_example
          exit when cur_dim%notfound; --if there is no more data in the cursor then exit
          dbms_output.put_line('Calculating aggregates for column (dimension): '||rec_dim.columnname);
          load_dimension(rec_dim.columnname, p_i_data_filter_start, p_i_data_filter_end);
      end loop;
      close cur_dim; --close the cursor

  end;

  -----------------------------------------------------------------------------
  procedure load_dimension(  p_i_dim_columnname varchar2,
                             p_i_data_filter_start date, 
                             p_i_data_filter_end date
                           ) is
      i number;                  
  begin 
      -- inserting data
      -- create rows - base on max/min times of metrics collected (first time can take longer)
      dbms_output.put_line('Extending aggregates table...');
      insert into am_all_dims_norm_aggs(ts, yyyy, mm, dd, hh, mi, tss)
          select sp.ts, to_char(sp.ts, 'YYYY') as yyyy, to_char(sp.ts, 'MM') as mm, to_char(sp.ts, 'DD') as dd, to_char(sp.ts, 'HH24') as hh, to_char(sp.ts, 'MI') as mi, 
                 trunc(to_char(sp.ts, 'SSSSS') / 15) * 15 as tss 
          from (
              select trunc((select min(r.ts) from am_raw_metric r), 'MI') + x / 24 / 60 / 4 as ts, p.x
              from   am_pivot p 
              where  p.x >= ((select nvl(max(a.ts),sysdate-(366*100)) from am_all_dims_norm_aggs a) - (select min(r.ts) from am_raw_metric r)) * 24 * 60 * 4
              and    p.x <  ((select max(r.ts) from am_raw_metric r) - (select min(r.ts) from am_raw_metric r)) * 24 * 60 * 4
              -- filter
              --and    p.x >=  (p_i_data_filter_start - (select min(r.ts) from am_raw_metric r)) * 24 * 60 * 4
              --and    p.x <=  (p_i_data_filter_end   - (select min(r.ts) from am_raw_metric r)) * 24 * 60 * 4
              --and    p.x >=  ((to_date('2011-12-04 21:30:15', 'YYYY-MM-DD HH24:MI:SS')) - (select min(r.ts) from am_raw_metric r)) * 24 * 60 * 4
              --and    p.x <=  ((to_date('2011-12-09 12:12:30', 'YYYY-MM-DD HH24:MI:SS')) - (select min(r.ts) from am_raw_metric r)) * 24 * 60 * 4
          ) sp , am_all_dims_norm_aggs a
          --where not exists (select 1 from am_all_dims_norm_aggs a where a.ts = ts) 
          where  sp.ts = a.ts(+) and a.ts is null 
          order by 1;
      commit;

      dbms_output.put_line('Calculating aggregates and updating table...');
      for i in 0..(p_i_data_filter_end-p_i_data_filter_start)*24-1 loop
          load_dimension_hh(p_i_dim_columnname, p_i_data_filter_start + i/24);
      end loop;
      
  end;

  -----------------------------------------------------------------------------
  procedure load_dimension_hh(  p_i_dim_columnname varchar2,
                                p_i_data_filter_start date
                              ) is
      update_sql varchar2(32000);
      p_i_data_filter_end date := p_i_data_filter_start + 1/24;
  begin
      dbms_output.put_line('Calculating aggregates of '||p_i_dim_columnname||' for data collected at '||to_char(p_i_data_filter_start, 'YYYY-MM-DD HH24:MI'));
      if substr(p_i_dim_columnname, 1, 1) in ('r', 's') then  
          -- resources and slas - update table - adding aggregated data to table
          insert into am_temp_resource(yyyy, mm, dd, hh, mi, tss, r_avg)
              select to_char(ts, 'YYYY') as yyyy, to_char(ts, 'MM') as mm, to_char(r.ts, 'DD') as dd, to_char(r.ts, 'HH24') as hh, to_char(r.ts, 'MI') as mi,
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15 as tss, 
                     avg(r.metricvalue) as r
              from   am_raw_metric r, am_all_dims_norm_aggs_meta m
              where  r.artifactcode = m.artifactcode and r.metrictypecode = m.metrictypecode and r.resourcename = m.resourcename
              and    m.columnname = p_i_dim_columnname --'r131'
              and    r.ts >=  p_i_data_filter_start --to_date('2011-12-05 21', 'YYYY-MM-DD HH24')
              and    r.ts <   p_i_data_filter_end --to_date('2011-12-05 22', 'YYYY-MM-DD HH24')
              ----and    r.ts > max_ts in agg tab
              ----and    r.ts between a.ts and a.ts + 1/24/60/4 
              group by 
                     to_char(ts, 'YYYY'), to_char(ts, 'MM'), to_char(r.ts, 'DD'), to_char(r.ts, 'HH24'), to_char(r.ts, 'MI'),
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15;
          update_sql :=
          'update (select a.'||p_i_dim_columnname||' as d, t.r_avg
                  from   am_all_dims_norm_aggs a, am_temp_resource t
                  where  a.yyyy = t.yyyy and a.mm = t.mm and a.dd = t.dd and a.hh = t.hh and a.mi = t.mi and a.tss = t.tss
                  and    a.ts >=  :1
                  and    a.ts <   :2)
          set d = r_avg';
          --dbms_output.put_line('Calculating aggregates SQL: '||update_sql);
          execute immediate update_sql using p_i_data_filter_start, p_i_data_filter_end;
      else 
          -- actions - update table - adding aggregated data to table
          insert into am_temp_resource(yyyy, mm, dd, hh, mi, tss, a_sum, a_cnt)
              select to_char(ts, 'YYYY') as yyyy, to_char(ts, 'MM') as mm, to_char(r.ts, 'DD') as dd, to_char(r.ts, 'HH24') as hh, to_char(r.ts, 'MI') as mi,
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15 as tss, 
                     sum(r.metricvalue), count(1)
              from   am_raw_metric r, am_all_dims_norm_aggs_meta m
              where  r.artifactcode = m.artifactcode and r.metrictypecode = m.metrictypecode and r.resourcename = m.resourcename
              and    r.entrypoint = 'EXIT'
              and    m.columnname = p_i_dim_columnname
              and    r.ts >=  p_i_data_filter_start --to_date('2011-12-05 21', 'YYYY-MM-DD HH24')
              and    r.ts <   p_i_data_filter_end --to_date('2011-12-05 22', 'YYYY-MM-DD HH24')
              ----and    r.ts > max_ts in agg tab
              ----and    r.ts between a.ts and a.ts + 1/24/60/4 
              group by 
                     to_char(ts, 'YYYY'), to_char(ts, 'MM'), to_char(r.ts, 'DD'), to_char(r.ts, 'HH24'), to_char(r.ts, 'MI'),
                     trunc(to_char(r.ts, 'SSSSS') / 15) * 15;
          update_sql :=
          'update (select a.'||p_i_dim_columnname||' as d_sum, t.a_sum --a.a121sum as a_sum, a.a121cnt as a_cnt, t.r
                  from   am_all_dims_norm_aggs a, am_temp_resource t
                  where  a.yyyy = t.yyyy and a.mm = t.mm and a.dd = t.dd and a.hh = t.hh and a.mi = t.mi and a.tss = t.tss
                  and    a.ts >=  :1
                  and    a.ts <   :2)
          set d_sum = a_sum --, d_cnt = a_cnt ';
          execute immediate update_sql using p_i_data_filter_start, p_i_data_filter_end;                     
      end if;
      /*
      update_sql :=
      'update am_all_dims_norm_aggs a set a.'||p_i_dim_columnname||' =
         (select r
          from (
              select to_char(ts, ''YYYY'') as yyyy, to_char(ts, ''MM'') as mm, to_char(r.ts, ''DD'') as dd, to_char(r.ts, ''HH24'') as hh, to_char(r.ts, ''MI'') as mi,
                     trunc(to_char(r.ts, ''SSSSS'') / 15) * 15 as tss, 
                     avg(r.metricvalue) as r
              from   am_raw_metric r, am_all_dims_norm_aggs_meta m
              where  r.artifactcode = m.artifactcode and r.metrictypecode = m.metrictypecode and r.resourcename = m.resourcename
              and    m.columnname = :1
              --where  r.artifactcode = ''OS'' and r.metrictypecode = ''OSIO'' and r.resourcename = ''DiskQueue:''
              and    r.ts >=  :2
              and    r.ts <   :3
              ----and    r.ts > max_ts in agg tab
              ----and    r.ts between a.ts and a.ts + 1/24/60/4 
              group by 
                     to_char(ts, ''YYYY''), to_char(ts, ''MM''), to_char(r.ts, ''DD''), to_char(r.ts, ''HH24''), to_char(r.ts, ''MI''),
                     trunc(to_char(r.ts, ''SSSSS'') / 15) * 15
               )t
          where a.yyyy = t.yyyy and a.mm = t.mm and a.dd = t.dd and a.hh = t.hh and a.mi = t.mi and a.tss = t.tss)
      where  a.ts >=  :4
      and    a.ts <   :5';
      dbms_output.put_line('Calculating aggregates SQL: '||update_sql);
      execute immediate update_sql 
      using p_i_dim_columnname, p_i_data_filter_start, p_i_data_filter_end, p_i_data_filter_start, p_i_data_filter_end;
      */
      commit;
  end;

  -----------------------------------------------------------------------------
  function dim_column_name( p_i_artifactcode varchar2,
                            p_i_am_ald_id number) return varchar2 is
      columnname am_all_dims_norm_aggs_meta.columnname%type;
  begin
      if p_i_artifactcode in ('OS', 'JVM') then
        columnname := 'r'||p_i_am_ald_id; -- resources
      elsif p_i_artifactcode in ('APP') then
        columnname := 'a'||p_i_am_ald_id; -- actions
      elsif p_i_artifactcode in ('SYNTH') then
        columnname := 's'||p_i_am_ald_id; -- synthetic resource, SLA
      end if;
      return columnname;
  end;


end am_control_fuzzy_hist;
/
