
BEGIN 
  rs_ttc.am_allmetric_mngr.raw_load_to_allmetric;
  COMMIT;
END;

ORA-01555 -- !!!


SELECT COUNT(*) FROM rs_ttc.am_raw_metric;

SELECT COUNT(*) FROM rs_ttc.am_metricsdata;

SELECT COUNT(*) FROM vam_metricsdata_cal;

SELECT DISTINCT c.artifactname, c.metricname FROM vam_metricsdata_cal c

SELECT c.year, c.month, c.day, COUNT(*)
FROM vam_metricsdata_cal c
WHERE c.metricname = 'Java JMX'
GROUP BY c.year, c.month, c.DAY
ORDER BY 1,2,3;

SELECT * 
FROM  vam_metricsdata_cal c
WHERE c.metricname = 'Java JMX'
AND   c.YEAR = 2010 AND c.MONTH = 1 AND c.DAY = 5
AND   c.resourcename LIKE 'java.lang:type=Memory%';



-- aggregation 
INSERT INTO 
SELECT c.YEAR, c.MONTH, c.DAY, c.hour, COUNT(*) --DISTINCT c.resourcename
FROM  vam_metricsdata_cal c
WHERE c.metricname = 'Java JMX'
--AND   c.YEAR = 2010 AND c.MONTH = 1 AND c.DAY = 5
GROUP BY c.YEAR, c.MONTH, c.DAY, c.hour;


/*
    * Aggregation function
          o Aggregate metrics name (result of the aggregation process) 

    * Filters
          o Instance
          o Host
          o StartTime?/EndTime? metrics which are taken by the process 

    * Grouping
          o Instance
          o Host
          o Resource (can contain part of resources names - reg exp)
          o Source (can contain part of resources names - reg exp)
          o Time group (i.e. day, hour, quarter, minute) 
*/

-- DTD/XML aggregations process definitions
allmon-aggregation.dtd
<!ELEMENT aggregation (dimensions, measures)>
          <!ATTLIST aggregation
                    name CDATA #REQUIRED
                    metric_code CDATA #REQUIRED>
<!ELEMENT dimensions (dimension+)>
<!ELEMENT dimension (filter*)>
          <!ATTLIST dimension
                    name (instancename | hostname | calendar | resourcename | sourcename) #REQUIRED
                    level (year | month | day | twohour | hour | halfhour | quarter | fiveminutes | minute) >
<!ELEMENT filter (#PCDATA)>
          <!ATTLIST filter
                    value CDATA
                    true ( yes | no ) "yes">
<!ELEMENT measures (#PCDATA)>


<?xml version="1.0"?>
<!DOCTYPE aggregation SYSTEM "allmon-aggregation.dtd">
<aggregation name="new_aggregate_metric_tpe" metric_code="JVMJMX">
  <dimensions>
    <!-- mandatory -->
    <dimension name="instancename">
      <filter value="C:\apache-activemq-5.2.0\bin\../bin/run.jar:id=29028"/>
    </dimension>
    <dimension name="hostname">
      <filter value="LONWK274"/>
    </dimension>

    <!-- calendar -->
    <dimension name="calendar" level="quarter">
      <filter level="year" value="2009"/>
    </dimension>

    <!-- -->
    <dimension name="resourcename">
      <filter method="like" value="java.lang:type=Memory:HeapMemoryUsage%">
      <!--<filter method="regexp" value="java.lang:type=Memory:HeapMemoryUsage.*">-->
    </dimension>
    <!--<dimension name="sourcename"/>-->

  </dimensions>
  <measures>
    <method name="avg">
  </measures>
</aggregation>


-- IMPORTANT!!!
-- reevaluate link between metrics type table and resources and sources:
-- a. maybe metrics type should be directly connected to metricsdata, then resources and sources could be shared across many different metrics types
-- b. the link stays, so resources and sources are not shared, therafore all correlating queries have to match resources and sources


-- 1. add a new *aggregated* metric type


-- 2. add new resources and sources (regardless soft/sharp filtering is in use) for a new *aggregated* metric type
INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename) --, resourcecode, unit)
        SELECT am_rsc_seq.NEXTVAL, am_mty_id, resourcename
        FROM (
                SELECT DISTINCT amt.am_mty_id, arm.resourcename
                FROM am_raw_metric arm
                LEFT OUTER JOIN am_resource ar ON (arm.resourcename = ar.resourcename)
                INNER JOIN am_metrictype amt ON (arm.metrictypecode = amt.metriccode)
                WHERE ar.am_rsc_id IS NULL
                AND   arm.resourcename IS NOT NULL -- resource field is required
                --WHERE rm.ts BETWEEN p_i_datetime_start AND p_i_datetime_end
        );

INSERT INTO am_source(am_src_id, am_mty_id, sourcename) --, sourcecode)
        SELECT am_src_seq.NEXTVAL, am_mty_id, sourcename
        FROM (
                SELECT DISTINCT amt.am_mty_id, arm.sourcename
                FROM am_raw_metric arm
                LEFT OUTER JOIN am_source asr ON (arm.sourcename = asr.sourcename)
                INNER JOIN am_metrictype amt ON (arm.metrictypecode = amt.metriccode)
                WHERE asr.am_src_id IS NULL
                AND   asr.sourcename IS NOT NULL -- source field is required
                --WHERE rm.ts BETWEEN p_i_datetime_start AND p_i_datetime_end
        );


-- 3. fill out metricsdata table - using dynamically created sql merge statement
MERGE INTO am_metricsdata md
USING (
        SELECT  v.am_ins_id, v.instancename, v.am_hst_id, hostname, hostip,
                group_resourcename, group_sourcename,
                year, month, DAY, hour,
                quarter,
                -- aggregation function
                AVG(metricvalue) AS aggregatedvalue
                --MIN(metricvalue) AS aggregatedvalue,
                --MAX(metricvalue) AS aggregatedvalue,
                --COUNT(*) AS aggregatedvalue
        FROM   (
                SELECT v.am_ins_id, v.instancename, v.am_hst_id, v.hostname, v.hostip,
                       -- resource
                       v.resourcename, 'java.lang:type=Memory:HeapMemoryUsage%' AS group_resourcename, -- allways the same as filter on resource
                       -- source
                       v.sourcename, v.sourcename AS group_sourcename, -- allways the same as filter on source
                       -- Time group
                       v.year, v.month, v.DAY, v.hour, v.minute,
                       trunc(v.hour / 2) AS twohour, trunc(v.minute / 30) AS halfhour, trunc(v.minute / 15) AS quarter, trunc(v.minute / 5) AS fiveminutes,
                       -- pre function fro aggregation
                       v.metricvalue
                FROM   vam_metricsdata_cal v
                WHERE  v.metriccode = 'JVMJMX' -- v.artifactcode = 'JVM'
                AND    v.instancename = 'C:\apache-activemq-5.2.0\bin\../bin/run.jar:id=29028'
                AND    v.hostname = 'LONWK274'
                --AND    v.caldate BETWEEN starttime AND endtime
                --AND    v.resourcename = 'java.lang:type=Memory:HeapMemoryUsage/used' -- sharp filtering - no grouping (cannonical form not used)
                AND    v.resourcename LIKE 'java.lang:type=Memory:HeapMemoryUsage%' -- soft filtering - grouping (use of cannonical structure)
                --AND    REGEXP_LIKE(v.resourcename, 'java.lang:type=Memory:HeapMemoryUsage.*') - very soft filtering - grouping
                )
        GROUP BY v.am_ins_id, v.instancename, v.am_hst_id, hostname, hostip,
                group_resourcename, group_sourcename, 
                -- Time group
                year, month, DAY, hour,
                quarter
        --ORDER BY 1,2,3,4,5,6,7,8,9,10;
) sel
ON (md.am_ins_id = sel.am_ins_id 
    AND md.am_hst_id = sel.am_hst_id 
    AND md.am_rsc_id = ...
    AND md.am_src_id = ...
    AND md.am_cal_id = ...
    AND md.am_tim_id = ...)
WHEN MATCHED THEN
    UPDATE SET 
    md.am_rme_id, md.am_ins_id, md.am_hst_id, md.am_rsc_id, md.am_src_id, 
    md.am_cal_id, md.am_tim_id, 
    md.metricvalue = sel.aggregatedvalue, md.ts, md.loadts, md.observation_id
    --DELETE WHERE -- not used!
WHEN NOT MATCHED THEN
    INSERT (am_met_id, am_rme_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts, loadts, observation_id)
    VALUES am_met_seq.NEXTVAL, sel.am_rme_id, sel.am_ins_id, sel.am_hst_id, sel.am_rsc_id, sel.am_src_id, sel.am_cal_id, sel.am_tim_id, sel.metricvalue, sel.ts, SYSDATE, NULL
;
