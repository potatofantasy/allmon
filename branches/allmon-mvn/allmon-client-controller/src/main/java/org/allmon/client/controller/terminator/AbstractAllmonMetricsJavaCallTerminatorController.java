package org.allmon.client.controller.terminator;

import java.util.HashMap;
import java.util.Map;

import org.allmon.client.controller.MetricsDataStore;
import org.allmon.client.controller.neuralrules.Resource;
import org.allmon.client.controller.rules.State;
import org.allmon.client.controller.terminator.allmon.AllmonMetricsReceiver;
import org.allmon.common.MetricMessage;


public abstract class AbstractAllmonMetricsJavaCallTerminatorController extends AbstractJavaCallTerminatorController {

	// TODO get data from Aggregator (for controller use queue), raw metrics and SLA calculated values
	// TODO point to controlled application scope metrics retriever
	
	protected static final AllmonMetricsReceiver allmonMetricsReceiver = new AllmonMetricsReceiver();

	public Map<String, State> createSystemState(MetricsDataStore metricsDataStore, 
			Resource[] resources, long timeScope) {
		// for all resources names search metrics in last milliseconds set be timeScope
		Map<String, State> currentState = new HashMap<String, State>();
		// gather all needed for control decisions resources
		for (Resource resource : resources) {
			String resourceName = resource.getName();
			MetricMessage metric = metricsDataStore.getLatest(resourceName, timeScope);
			// taking metric values and creating system state object
			if (metric != null) {
				State state = new State(resourceName, metric.getMetricValue());
				currentState.put(resourceName, state);
			}
		}
		return currentState;
	}

/*	
	public Map<String, State> createSystemState(MetricsDataStore metricsDataStore, String[] resources, long timeScope) {
		// for all resources names search metrics in last milliseconds set be timeScope
//		List<State> currentState = new ArrayList<State>();
		Map<String, State> currentState = new HashMap<String, State>();
		
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
					
					// taking metric values and creating system state object
					if (metric != null) {
						State state = new State(resource, metric.getMetricValue());
						//currentState.add(state);
						currentState.put(resource, state);
					}
				}
//			}
//		}
		return currentState;
	}
*/
}
