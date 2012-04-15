Create table Sales_EMATable (product, month, amount)
 as
 Select 'A', date '2010-01-01', 40 from dual union all
  Select 'A', date '2010-02-01', 35 from dual union all
  Select 'A', date '2010-03-01', 27 from dual union all
  Select 'A', date '2010-04-01', 30 from dual union all
  Select 'A', date '2010-05-01', 32 from dual union all
  Select 'A', date '2010-06-01', 30 from dual union all
  Select 'A', date '2010-07-01', 35 from dual union all
  Select 'A', date '2010-08-01', 37 from dual union all
  Select 'A', date '2010-09-01', 20 from dual union all
  Select 'A', date '2010-10-01', 25 from dual union all
  Select 'A', date '2010-11-01', 27 from dual union all
  Select 'A', date '2010-12-01', 30 from dual union all
  Select 'B', date '2010-01-01', 0 from dual union all
  Select 'B', date '2010-02-01', 60 from dual union all
  Select 'B', date '2010-03-01', 40 from dual union all
  Select 'B', date '2010-04-01', 30 from dual union all
  Select 'B', date '2010-05-01', 55 from dual union all
  Select 'B', date '2010-06-01', 45 from dual union all
  Select 'B', date '2010-07-01', 30 from dual union all
  Select 'B', date '2010-08-01', 20 from dual union all
  Select 'B', date '2010-09-01', 60 from dual union all
  Select 'B', date '2010-10-01', 40 from dual union all
  Select 'B', date '2010-11-01', 30 from dual union all
  Select 'B', date '2010-12-01', 30 from dual;

-- 
SELECT month, SUM(amount) AS month_amount,
       AVG(SUM(amount)) OVER (ORDER BY month ROWS BETWEEN 3 PRECEDING AND CURRENT ROW) AS moving_average
FROM   Sales_EMATable
GROUP BY month
ORDER BY month;

select product, month, amount, round(ema,3) "Exponential Moving Average"
    from Sales_EMATable
   model partition by (product)
         dimension by (month)
         measures (amount,0 ema)
         ( ema[any] order by month
           = nvl2
             ( ema[add_months(cv(),-1)]
             , ( 0.7 * (amount[cv()] - ema[add_months(cv(),-1)])) + ema[add_months(cv(),-1)]
             , amount[cv()]
             )
         )
   order by product, month ;
   
   -------------------------------

drop table w;
create table W as
select  1 as SortKey,2 as Val from dual union all
select  2,null from dual union all
select  3,null from dual union all
select  4,null from dual union all
select  5,   4 from dual union all
select  6,null from dual union all
select  9,null from dual union all
select 11,   6 from dual union all
select 12,null from dual union all
select 14,null from dual union all
select 16,   5 from dual union all
select 17,null from dual union all
select 20,   3 from dual union all
select 21,null from dual union all
select 22,   4 from dual;

-- lead 2 (https://forums.oracle.com/forums/thread.jspa?threadID=918369)
select SortKey,Val,
max(Val) over(order by aboveCnt range between 2 preceding and 2 preceding) as Lag2,
max(Val) over(order by belowCnt range between 2 preceding and 2 preceding) as Lead2
from (select SortKey,Val,
      count(Val) over(order by SortKey rows between unbounded preceding and 1 preceding) aboveCnt,
      count(Val) over(order by SortKey rows between 1 following and unbounded following) belowCnt
         from W)
order by SortKey;   
      
-- lag1/lead 1 + case (ignore nulls)
select SortKey,Val, 
       case when val is null then max(Val) over(order by aboveCnt range between 1 preceding and 1 preceding) else val end as Lag1,
       case when val is null then max(Val) over(order by belowCnt range between 1 preceding and 1 preceding) else val end as Lead1
from (select SortKey,Val, 
             count(Val) over(order by SortKey rows between unbounded preceding and 1 preceding) aboveCnt,
             count(Val) over(order by SortKey rows between 1 following and unbounded following) belowCnt from W)
order by SortKey;

-- lerp - http://en.wikipedia.org/wiki/Linear_interpolation
select s.*,
       case when val is null then (y0 + (SortKey - x0) * (y1 - y0)/(x1 - x0))
            else val end as lerp
from (
      select SortKey,Val, 
             case when val is null then max(sortkey) over(order by aboveCnt range between 1 preceding and 1 preceding) else sortkey end as x0,
             case when val is null then min(sortkey) over(order by belowCnt range between 1 preceding and 1 preceding) else sortkey end as x1,
             case when val is null then max(Val) over(order by aboveCnt range between 1 preceding and 1 preceding) else val end as y0, --Lag1,
             case when val is null then max(Val) over(order by belowCnt range between 1 preceding and 1 preceding) else val end as y1 --Lead1
      from (select SortKey,Val, 
                   count(Val) over(order by SortKey rows between unbounded preceding and 1 preceding) aboveCnt,
                   count(Val) over(order by SortKey rows between 1 following and unbounded following) belowCnt from W)
) s order by SortKey;
