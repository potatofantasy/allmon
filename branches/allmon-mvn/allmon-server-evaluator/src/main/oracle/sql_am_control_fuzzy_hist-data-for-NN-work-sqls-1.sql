

begin 
  am_control_fuzzy_hist.refresh_dimensions;
end;

begins
  am_control_fuzzy_hist.load_dimensions(
                                '%', -- all resources
                                p_i_data_filter_start => to_date('2012-04-10 23', 'YYYY-MM-DD HH24'),
                                p_i_data_filter_end => to_date('2012-04-10 23:30', 'YYYY-MM-DD HH24:MI'));
end;

begin 
  am_control_fuzzy_hist.load_dimension(
                                's184', --'r137',
                                p_i_data_filter_start => to_date('2012-04-14 17', 'YYYY-MM-DD HH24'),
                                p_i_data_filter_end =>   to_date('2012-04-14 22', 'YYYY-MM-DD HH24'));
end;

begin 
  am_control_fuzzy_hist.load_dimension(
                                'r128', --'r130', --'r137',
                                p_i_data_filter_start => to_date('2011-12-05', 'YYYY-MM-DD HH24'),
                                p_i_data_filter_end => to_date('2011-12-07', 'YYYY-MM-DD HH24:MI'));
end;

-----------------------------------------------------------------------------------------------------

select * from am_all_dims_norm_aggs_meta;

select * from am_all_dims_norm_aggs where s184 is not null order by 1;

select * from am_all_dims_norm_aggs a
where  a.ts > to_date('2011-12-05 00', 'YYYY-MM-DD HH24')
and    a.ts < to_date('2011-12-07 00', 'YYYY-MM-DD HH24') order by 1;

select * from am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-07', 'YYYY-MM-DD')
and    a.ts <= to_date('2012-04-08', 'YYYY-MM-DD') order by 1;

-- TODO add sum, cnt, extcnt to Actions!!
-- eceptions can be also a synth metric
select ts, a121 as act, r135 as cpuuser, r137 as diskqueue, r150 as memusedp, a183 as termact,
       s163 sla1, s164 as sla3, s184 as sla10term
from   am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-07 22', 'YYYY-MM-DD HH24')
and    a.ts <= to_date('2012-04-07 23', 'YYYY-MM-DD HH24') order by 1;

-----------------------------------------------------------------------------------------------------
/*
NN adaptive controller in ASM environment
The algorithm proposed:
1. Select 'good', and possibly 'bad' control rules (decisions) from the past.
- It is a very open area - the decision of whether a control decisions were good or bad can be taken on basis of a statistical model, or due the fact it is a classification problem, by an another specialised NN.
- For the benefit of further simulations I picked up data which I 'believed' represent good overall system response - considering SLA and control actions.
2. (Re-)train the networks.
- Apparently I was not able to find a topology for a situation where i have both training sets data in one network, adding ControlEvaluation dimension (good/bad values) ruined the learning phase - even with huge topologies i was not able to get any meaningful results (I presume this is related to some sort of 'xor' problem).
3. Serialize and send learned networks to actuator process(es).
4. Wait dT time (i.e. 2min - selecting and training time is the main concern).
*/

-- re 0  -- okreslenie wagi wymiaru dla controli!!! - selekcja wymiarow, ktore maja wplyw na kontrole

--------------------------------------------
-- re 1.0 -- fiding areas where control is needed -- where total slas are high 
          -- trained nn will reproduce anything, the point is to train it in good things (minimising sla footprint)

-- termination as a controll lead to releasing reasources which would be engaged in a normla functional execution
-- so it is always related to lowering execution-time-SLAs penalties of not terminated action
select ts, a121 as act, 
       r135 as cpuuser, r130, r137 as diskqueue, r150 as memusedp, a183 as termact,
       --trunc(r135, 1) as cpuuser, trunc(r137, 1) as diskqueue, trunc(r150, 1) as memusedp, a183 as termact,
       s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal
from   am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
and    a.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
--and    a.s184 is not null -- with control applied
and    a.s184 is null -- without any control applied
order by slatotal desc; --order by sla3 desc 
--order by 1;

     -- 1.1 good control -- select highest total slas where control was not applied (termination control penalties are zero)
     select ts, a121 as act, 
             r135 as cpuuser, r130 as cpu, r137 as diskqueue, r150 as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for a given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-12 23', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-13 01', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is null -- no control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-12 23', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-13 01', 'YYYY-MM-DD HH24')
      and    a.s184 is null -- no control action taken
      and    nvl(s185,0) + nvl(s164,0) > 0 -- sum of slas are greater than zero
      order by avgslatotal desc nulls last

     -- 1.2 good control -- select highest total slas where control was applied, but costs of termination penalties are lower than rest of slas
     -- (costs of control are not overtaking costs of slas penalties)
     select ts, a121 as act, 
             r135 as cpuuser, r130 as cpu, r137 as diskqueue, r150 as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for a given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              --and    a1.s184 is null -- no control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
      and    a.s184 is not null -- control action taken
      and    nvl(s185,0) + nvl(s164,0) > nvl(s184,0) -- sum of slas are higher than termination penalties
      order by avgslatotal desc nulls last -- highest average of total sla for a given system state neighbourhood 

     -- 1.1 bad control -- select lowest total slas where control was not applied (termination control penalties are zero)
     select ts, a121 as act, 
             r135 as cpuuser, r130 as cpu, r137 as diskqueue, r150 as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for a given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-12 23', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-13 01', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is null -- no control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-12 23', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-13 01', 'YYYY-MM-DD HH24')
      and    a.s184 is null -- no control action taken
      and    nvl(s185,0) + nvl(s164,0) = 0 -- sum of slas are greater than zero
      order by avgslatotal asc nulls last 

     -- 1.2 bad control -- select lowest total slas where control was applied, and costs of termination penalties are higer than rest of slas
     -- (costs of control are overtaking costs of slas penalties == too strong control)
     select ts, a121 as act, 
             r135 as cpuuser, r130 as cpu, r137 as diskqueue, r150 as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for a given system state neighbourhood
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              --and    a1.s184 is null -- no control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
      and    a.s184 is not null -- control action taken
      and    nvl(s185,0) + nvl(s164,0) <= nvl(s184,0) -- sum of slas are lower than termination penalties (costs of control are overtaking costs of slas penalties == too strong control)
      order by avgslatotal asc nulls last  -- lowest average of total sla for a given system state neighbourhood     
      
-- re 1a
select * from am_all_dims_norm_aggs where s165 > 10
order by 1;

select ts, a121 as act, 
       r135 as cpuuser, r137 as diskqueue, r150 as memusedp, a183 as termact,
       --trunc(r135, 1) as cpuuser, trunc(r137, 1) as diskqueue, trunc(r150, 1) as memusedp, a183 as termact,
       s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal
from   am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
and    a.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
--and    a.s184 is not null 
order by slatotal desc; --order by 1;



-- COMPARISON!! - learning on mistakes - is a basisof knowledge exploring!
-- okreslamy warunki brzegowe - dla dwoch NNs
select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal
from   am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-07 22', 'YYYY-MM-DD HH24')
and    a.ts <= to_date('2012-04-07 23', 'YYYY-MM-DD HH24')  
and    r135 between 0.75 - 0.1 and 0.75 + 0.1 -- 0.709 - 0.1 and 0.709 + 0.1 -- cpuuser
and    r137 between 4 - 2 and 4 + 2  --6.5 - 1 and 6.5 + 1 -- diskqueue
and    r150 between 79 - 10 and 79 + 10 --77.657 - 10 and 77.657 + 10 -- memusedp
and    a.s184 is null -- no control actions taken

-- good decissions
-- good control decision is such which makes the situation better - SLAs are lower for the same state parameters (n-cube neighberhood)-->normalization!
select  ts, act, cpuuser, diskqueue, memusedp, termact, sla10term, slatotal, avgslatotal, 1 as control
from (select ts, a121 as act, 
             r135 as cpuuser, r137 as diskqueue, r150 as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for a given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is null -- no control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
      and    a.s184 is not null -- control action taken
      )
where slatotal < avgslatotal -- sla total with control was lower than without for such system state 
order by slatotal - avgslatotal;

select  ts, act, cpuuser, diskqueue, memusedp, termact, sla10term, slatotal, avgslatotal, 0 as control -- better to not take any action for such states
from (select ts, a121 as act, 
             r135 as cpuuser, r137 as diskqueue, r150 as memusedp, a183 as termact,
             --trunc(r135, 1) as cpuuser, trunc(r137, 1) as diskqueue, trunc(r150, 1) as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is not null -- control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-12 18', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-12 19', 'YYYY-MM-DD HH24')
      and    a.s184 is null -- no control action taken
      )
where slatotal < avgslatotal -- sla total without control was lower than with any control for such system state 
order by slatotal - avgslatotal;

-- bad decissions - 
-- bad control decision is such which makes the situation worse - SLAs are higher for the same state parameters (n-cube neighberhood)-->normalization!
select  ts, act, cpuuser, diskqueue, memusedp, termact, sla10term, slatotal, avgslatotal, cntslatotal, 1 as control
from (select ts, a121 as act, 
             r135 as cpuuser, r137 as diskqueue, r150 as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for a given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-10 23', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-11 00', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is null -- no control actions taken
             ) as avgslatotal,
             (select count(*) -- count of total sla for a given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-10 23', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-11 00', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is null -- no control actions taken
             ) as cntslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-10 23', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-11 00', 'YYYY-MM-DD HH24')
      and    a.s184 is not null -- control action taken
      )
where slatotal > avgslatotal -- sla total with control was greater than without
order by avgslatotal - slatotal;

select  ts, act, cpuuser, diskqueue, memusedp, termact, sla10term, slatotal, avgslatotal, 0 as control
from (select ts, a121 as act, 
             r135 as cpuuser, r137 as diskqueue, r150 as memusedp, a183 as termact,
             --trunc(r135, 1) as cpuuser, trunc(r137, 1) as diskqueue, trunc(r150, 1) as memusedp, a183 as termact,
             s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal,
             (select avg(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as avgslatotal -- average of total sla for given system state
              from   am_all_dims_norm_aggs a1
              where  a1.ts >= to_date('2012-04-10 23', 'YYYY-MM-DD HH24')
              and    a1.ts <= to_date('2012-04-11 00', 'YYYY-MM-DD HH24')
              and    a1.r135 between a.r135 - 0.1 and a.r135 + 0.1 -- cpuuser
              and    a1.r137 between a.r137 - 2 and a.r137 + 2  -- diskqueue
              and    a1.r150 between a.r150 - 10 and a.r150 + 10 -- memusedp
              and    a1.s184 is not null -- no control actions taken
             ) as avgslatotal
      from   am_all_dims_norm_aggs a
      where  a.ts >= to_date('2012-04-10 23', 'YYYY-MM-DD HH24')
      and    a.ts <= to_date('2012-04-11 00', 'YYYY-MM-DD HH24')
      and    a.s184 is null -- no control action taken
      )
where slatotal > avgslatotal -- sla total without control were greater than with any control
order by avgslatotal - slatotal; -- order by strenght of the comparison 

-- resolving conflicts between good and bad system states vectors for further training 
-- ommiting unstabilities areas - where a control decissions are not very stable (consequent) 
---- using wages of SLAs + removing proposed state points from train vectors ifconflicting in a close neighborhood

-- nn are trained with normalized data - under defined scopes of the data - if a system state value is outside of declared scope of a dimension
-- then acontrol action cant be taken, even ifthe values are extremly high - this state is monitored and stored, so future actions could use the
-- knowledge 



--------------------------------------------
-- re 1b. 

-- create synthetic resources, which will count exception (termination actions) in time - as penalty value 
-- comparison between good/bad SLAs will indicate bad control actions and metrics states for them   

begin 
    -- update SLA1
    am_control_fuzzy.update_sla(
         to_date('2012-04-10 22', 'YYYY-MM-DD HH24'), --p_i_window_start => + 1hour
         'SLA11: 1$ per every extra second over 2sec execution', --p_i_sla_resource_name => 
         'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]', --p_i_base_resource_like => 
         ' (case when (sum(metricvalue) - 2000) / 1000 < 5 then (sum(metricvalue) - 2000) / 1000 else 5 end) ', --p_i_select_sla_value_phrase =>  -- but no more than 5$ penalty
         'metricvalue > 2000', --p_i_where_metric_phrase => -- actions longer than 2 secs
         '1=1'); --p_i_having_phrase => 
    -- update SLA3
    am_control_fuzzy.update_sla(
         to_date('2012-04-10 22', 'YYYY-MM-DD HH24'), --p_i_window_start => + 1hour
         'SLA3: 10$ for every started second of an image processing longer by average than 10ms', --p_i_sla_resource_name => 
         'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/images/%.png [null]', --p_i_base_resource_like => 
         'ceil(10 * (count(*) * sum(r.metricvalue) / 1000))', --p_i_select_sla_value_phrase =>
         '1=1', --p_i_where_metric_phrase => -- no filter
         'avg(r.metricvalue) > 10'); --p_i_having_phrase => 
    -- update SLA10
    am_control_fuzzy.update_sla(
         to_date('2012-04-10 22', 'YYYY-MM-DD HH24'), --p_i_window_start => + 1hour
         'SLA10: 1$ per every teminated action', --p_i_sla_resource_name => 
         'org.allmon.client.controller.terminator.NeuralRulesJavaCallTerminatorController.terminate',
         --'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]', --p_i_base_resource_like => 
         'count(1)', --p_i_select_sla_value_phrase =>  -- but no more than 5$ penalty
         'exceptionbody is not null and sourcename = ''se.citerus.dddsample.application.impl.VoidLoadAdderImpl.generateIOLoad''', --p_i_where_metric_phrase => -- actions longer than 2 secs
         '1=1'); --p_i_having_phrase => 
    commit;
    am_control_fuzzy_hist.load_dimensions(
          '%', -- all resources
          p_i_data_filter_start => to_date('2012-04-10', 'YYYY-MM-DD'),
          p_i_data_filter_end => to_date('2012-04-11', 'YYYY-MM-DD'));
          
         
          
    commit;
end;

begin
    -- update SLA10
    am_control_fuzzy.update_sla(
         to_date('2012-04-14 22', 'YYYY-MM-DD HH24'), --p_i_window_start => + 1hour
         'SLA10: 1$ per every teminated action', --p_i_sla_resource_name => 
         'org.allmon.client.controller.terminator.NeuralRulesJavaCallTerminatorController.terminate',
         --'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/track [null]', --p_i_base_resource_like => 
         'count(1)', --p_i_select_sla_value_phrase =>  -- but no more than 5$ penalty
         'exceptionbody is not null and sourcename = ''se.citerus.dddsample.application.impl.VoidLoadAdderImpl.generateIOLoad''', --p_i_where_metric_phrase => -- actions longer than 2 secs
         '1=1'); --p_i_having_phrase => 
end;


select * from am_raw_metric m 
where m.resourcename = 'org.allmon.client.controller.terminator.NeuralRulesJavaCallTerminatorController.terminate'
and   m.ts >  to_date('2012-04-07 22', 'YYYY-MM-DD HH24') and ts <=  to_date('2012-04-07 22', 'YYYY-MM-DD HH24') + 1/24;

select * from am_raw_metric m 
where m.resourcename like 'ExampleFilter1//% [null]'
and   m.ts >  to_date('2012-04-07 22', 'YYYY-MM-DD HH24') and ts <=  to_date('2012-04-07 22', 'YYYY-MM-DD HH24') + 1/24;

select * from am_raw_metric m where m.resourcename = 'SLA1: 1$ per every extra second over 2sec execution';
select * from am_raw_metric m where m.resourcename = 'SLA11: 1$ per every extra second over 2sec execution';
select * from am_raw_metric m where m.resourcename = 'SLA3: 10$ for every started second of an image processing longer by average than 10ms';
select * from am_raw_metric m where m.resourcename = 'SLA10: 1$ per every teminated action' and m.ts > sysdate - 1/24;

select * from am_all_dims_norm_aggs_meta m = 

-----------------------------------------------------------------------------------------------------

select * from am_raw_metric m where m.ts > sysdate - 1/24 and m.artifactcode = 'APP'

select * from am_raw_metric m where m.ts > sysdate - 6/24 order by ts desc 

select * from am_raw_metric m where m.ts > sysdate - 6/24 and m.metrictypecode = 'JAVCLS' and m.entrypoint = 'EXIT' order by ts desc 
select * from am_raw_metric m where m.ts > sysdate - 6/24 and m.metrictypecode = 'ACTCLS' and m.entrypoint = 'EXIT' 
                                and m.resourcename = 'ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/ [null]' order by ts desc 

select * from am_raw_metric m where m.ts > sysdate - 6/24 and m.metrictypecode = 'JAVCLS' and m.exceptionbody is not null order by ts desc 

------------==============================================================================================
-- data analysis - simulations discussion

select ts, a121 as act, r135 as cpuuser, r130, r137 as diskqueue, r150 as memusedp, a183 as termact,
       s185 as sla1, s164 as sla3, s184 as sla10term, nvl(s185,0) + nvl(s164,0) + nvl(s184,0) as slatotal
from   am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-14 21', 'YYYY-MM-DD HH24')
and    a.ts <= to_date('2012-04-15 03', 'YYYY-MM-DD HH24')
--and    a.s184 is not null -- with control applied
--and    a.s184 is null -- without any control applied
--order by slatotal desc; --order by sla3 desc 
order by 1;

select a.yyyy, a.mm, a.dd, a.hh, trunc(a.mi/5),
       --ts, 
       sum(a121) as act, avg(r135) as cpuuser, avg(r130), avg(r137) as diskqueue, avg(r150) as memusedp, sum(a183) as termact,
       sum(a121) as act,
       sum(s185) as sla1, sum(s164) as sla3, sum(s184) as sla10term, sum(nvl(s185,0) + nvl(s164,0) + nvl(s184,0)) as slatotal
from   am_all_dims_norm_aggs a
where  a.ts >= to_date('2012-04-14 23', 'YYYY-MM-DD HH24')
and    a.ts <= to_date('2012-04-15 06', 'YYYY-MM-DD HH24')
--and    a.s184 is not null -- with control applied
--and    a.s184 is null -- without any control applied
group by a.yyyy, a.mm, a.dd, a.hh, trunc(a.mi/2)
order by 1, 2, 3, 4, 5;
