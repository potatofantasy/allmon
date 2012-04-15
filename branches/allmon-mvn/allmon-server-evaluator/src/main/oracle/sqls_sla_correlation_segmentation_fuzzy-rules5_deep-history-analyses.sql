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
     --and    r.resourcename = 'DiskQueue:' -- mostly very low
     --and    r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]' and r.entrypoint = 'EXIT'
     and    r.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]' and r.entrypoint = 'EXIT'
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




/*
The algorithm proposed:
1. Select dimensions (resources/actions) which values match with SLA violations, find points in time:
    - Using known from previous report segmentation technique in the sliding window (last 1hour data aggregated in 240 15sec buckets).
    - This step helps to find start criteria of the search, and focus on problem areas (SLA impacts).

2. Select points (historical time buckets) which has similar location to found points in step 1:
    - Similarity criteria of two points in the simplest form can be defined as lying in the normalized-space in the same hyper-cube neighbourhood of a given size (simpler to query than using Euclidean distance).
    - Trying to find 'hidden' relations, not directly visible after segmentation in the sliding window - but existing in historical data (i.e. memory consumption impacts CPU, slow changes of drive space on increase in IO ops).

3. Measure (analyse) distribution of dimensions-not-matching segments, for selected from history buckets:
    - Build distribution function (PDF/histogram) of values in selected points on each of dimensions (actions, resources). 
    - Induce Rules with more precise coordinates (where more coordinates dimensions are present in a rule):
    3a. Distribution is 'flat' - many different values were found - we can reduce the dim - no Rule with the dimension.
    3b. Distribution is 'not-flat' - take most common values (i.e. PDF > 0.5) - filtering-out out-liners - requery using most common values.
    3c. Same or similar values - dimension analysed is related to matched points dims - values can be used in the Rule definition.
*/

-- expert systems do not typically provide a definitive answer, but provide probabilistic recommendations.

-- rsolving contraditions byusing necessity and possibility values
