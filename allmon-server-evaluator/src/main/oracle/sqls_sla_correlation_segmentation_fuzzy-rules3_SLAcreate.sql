
create table am_metadata_sla (
  sla_id         number,
  sla_name       varchar2(100),
  
);


select * from am_raw_metric where resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 20ms';
select * from am_raw_metric where resourcename like 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]';
    
    -- p_i_sla_resource_name; --
    delete from am_raw_metric where resourcename = 'SLA3: 100$ for every started second of an image processing longer by average than 20ms';
    
    insert into am_raw_metric(am_rme_id, 
                              artifactcode, hostname, hostip, instancename, entrypoint, --threadname,
                              metrictypecode, resourcename, sourcename,
                              ts, metricvalue)
      select  am_rme_seq.nextval, 
              'SYNTH' as artifactcode, hostname, hostip, instancename, 'ENTRY' as entrypoint, 
              'ACTCLS-SLA' as metrictypecode, 'SLA3: 100$ for every started second of an image processing longer by average than 20ms' as resourcename, sourcename, 
              to_date(ts, 'YYYY-MM-DD HH24:MI:SS'), sla_value
      from (
        select -- artifactcode, hostname, hostip, instancename, entrypoint, threadname,
               hostname, hostip, instancename, 
               'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]' as sourcename, --r.resourcename as sourcename,
               to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS') as ts,
               avg(r.metricvalue), sum(r.metricvalue), count(*),
               ceil(100 * (count(*) * sum(r.metricvalue) / 1000)) as sla_value -- 10$ for every started second of an image processing longer by average than 20ms
        from  am_raw_metric r
        where r.resourcename like 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]'
        --and  r.ts >= to_date('2011-12-05 08:00:00', 'YYYY-MM-DD HH24:MI:SS') 
        --and  r.ts <= to_date('2011-12-05 23:00:00', 'YYYY-MM-DD HH24:MI:SS') 
        --and  r.metricvalue > 1000 -- longer than 1 second
        group by hostname, hostip, instancename, 
                 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]', --r.resourcename,
                 to_char(r.ts, 'YYYY-MM-DD HH24:MI:SS')
        having avg(r.metricvalue) > 10 -- cummulative 1 second average aggregates over 10ms of execution
        order by ts) sel;
