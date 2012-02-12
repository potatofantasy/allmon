package org.allmon.client.controller.terminator;

import java.util.ArrayList;
import java.util.List;

import org.allmon.client.controller.MetricsDataStore;
import org.allmon.client.controller.rules.Condition;
import org.allmon.client.controller.rules.Rule;
import org.allmon.client.controller.rules.RuleSet;
import org.allmon.client.controller.rules.State;
import org.allmon.client.controller.terminator.AbstractAllmonMetricsJavaCallTerminatorController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.allmon.common.MetricMessage;

public class ReadRulesJavaCallTerminatorController extends AbstractAllmonMetricsJavaCallTerminatorController {

	@Override
	public boolean terminate(ProceedingJoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		
		System.out.println("allmonMetricsReceiver.metricsDataStore.valuesCount(): " + 
				allmonMetricsReceiver.metricsDataStore.valuesCount());
		System.out.println("allmonMetricsReceiver.metricsDataStore.keysCount(): " + 
				allmonMetricsReceiver.metricsDataStore.keysCount());
		
//		if (methodName.length() > 6) {
//			return true;
//		} 
//		return false;
		
//		System.out.println("allmonMetricsReceiver.metricsDataStore.keySet(): " +
//				allmonMetricsReceiver.metricsDataStore.keySet());
		
//		System.out.println(call.getArgs().length);
		
//		if (call.getArgs().length > 0) {
//			return true;
//		}
		
		return terminate3(allmonMetricsReceiver.metricsDataStore);
	}
	
	///////////////////////////
	// termination based on Rules definition 
	// This object will be amended and synchronized by a remote analyzer process
	private static final RuleSet ruleSet = new RuleSet();
	static {
		Rule rule1 = new Rule();
		rule1.add(new Condition("TSIKORA-LAPTOP,10.1.69.61,monitoring.instance,OSCPU,CPU Combined:/", ">", 0.80));
		
//		Rule rule2 = new Rule();
//		rule2.add(new Condition("TSIKORA-LAPTOP,10.1.69.61,monitoring.instance,OSCPU,CPU Combined:/", ">", 0.7));
//		rule2.add(new Condition("TSIKORA-LAPTOP,10.1.69.61,monitoring.instance,OSIO,DiskQueue:", ">", 0.5));
//		
		// filling up rule-set
		ruleSet.add(rule1);
//		ruleSet.add(rule2);
	}
	
	public boolean terminate3(MetricsDataStore metricsDataStore) {
		String[] resources = ruleSet.getResources();
		List<State> currentState = createSystemState(metricsDataStore, resources, 10000);
		// checking termination condition
		boolean b = ruleSet.checkExceededRule(currentState);
		if (b) {
			System.out.println(">>>>>>>>>>>>>> action terminated");
		}
		return b;
	}

	public List<State> createSystemState(MetricsDataStore metricsDataStore, String[] resources, long timeScope) {
		// for all resources names search metrics in last milliseconds set be timeScope
		List<State> currentState = new ArrayList<State>();
//		//
//		Set<String> keySet = metricsDataStore.keySet();
//		String[] keyArray = keySet.toArray(new String[0]);
//		for (String key : keyArray) {
//			// check only recent metrics
//			long currentMillis = System.currentTimeMillis();
//			long metricMillis = 0;
//			try {
//				metricMillis = getMetricMillis(key);
//			} catch (Exception e) {
//			}
//			// metrics which were gathered maximum timeScope milliseconds ago
//			if (metricMillis >= currentMillis - timeScope) {
				
				// gather all needed for control decisions resources
				for (String resource : resources) {
					//TODO we need to "match" resources names with control rules and 
					//     resources (keys) collected metrics
					
					// TODO review following!! 
					// TODO currently the implementation based on the same resourceKeys in rule-set
//					String resourcePattern = ".*" + resource + ".*"; // i.e. ".*MetricType:OSCPU, Resource:CPU User Time.*"
//					List<String> matchingKeys = 
//						metricsDataStore.getMatchingResourceKeys(resourcePattern); // TODO remove .*'s
					
					MetricMessage metric = metricsDataStore.getLatest(resource, timeScope);
					
//					// taking metric values and creating system state object
					if (metric != null) {
						State state = new State(resource, metric.getMetricValue());
						currentState.add(state);
					}
					
				}
				
//			}
//		}
		return currentState;
	}
	
}
