----------------------------------------------------------
-- analysing deep-history - using found segments of saturation, with level of 0.8

-- all values are normalized in this table
select * from am_all_dims_norm_aggs a
where  a.ts > to_date('2011-12-05 12', 'YYYY-MM-DD HH24')
and    a.ts < to_date('2011-12-05 23', 'YYYY-MM-DD HH24') 
order by 1;


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

Note: 
-- expert systems do not typically provide a definitive answer, but provide probabilistic recommendations.
-- resolving contraditions by using necessity and possibility values
*/

select * from am_all_dims_norm_aggs_meta;

-- re 1 - just a simple saturations find base on SLAs noton segments
select * from am_all_dims_norm_aggs a
where  ts between to_date('2011-12-05 12', 'YYYY-MM-DD HH24') and to_date('2011-12-05 23', 'YYYY-MM-DD HH24') 
and    s165 is not null
order by 1;

-- re 2
select * from am_all_dims_norm_aggs a
where  ts between to_date('2011-12-05 12', 'YYYY-MM-DD HH24') and to_date('2011-12-05 23', 'YYYY-MM-DD HH24') 
and    a.a121 between 204 - 50 and 204 + 50 -- action -- 2011-12-05 12:44:15 - 2011-12-05 12:45:45
and   a.r130 between 0.83 - 0.15 and 0.83 + 0.15 -- resource: cpu
order by 1;

-- re 3
select a.r129, count(*) 
from   am_all_dims_norm_aggs a
where  ts between to_date('2011-12-05 12', 'YYYY-MM-DD HH24') and to_date('2011-12-05 23', 'YYYY-MM-DD HH24') 
and    a.a121 between 204 - 50 and 204 + 50 -- action -- 2011-12-05 12:44:15 - 2011-12-05 12:45:45
and    a.r130 between 0.83 - 0.15 and 0.83 + 0.15 -- resource: cpu
group  by a.r129 
order by 1;



