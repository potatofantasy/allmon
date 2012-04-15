package org.allmon.client.controller.terminator;

import java.util.Map;

import org.allmon.client.controller.MetricsDataStore;
import org.allmon.client.controller.neuralrules.Resource;
import org.allmon.client.controller.rules.Condition;
import org.allmon.client.controller.rules.Rule;
import org.allmon.client.controller.rules.RuleSet;
import org.allmon.client.controller.rules.State;
import org.aspectj.lang.ProceedingJoinPoint;

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
		String[] resourceNames = ruleSet.getResources(); // TODO refactor to Resouces 
		Resource[] resources = new Resource[resourceNames.length];
		for (int i = 0; i < resourceNames.length; i++) {
			resources[i] = new Resource(resourceNames[i], 0, 1);
		}
		Map<String, State> currentState = createSystemState(metricsDataStore, resources, 10000);
		// checking termination condition
		boolean b = ruleSet.checkExceededRule(currentState);
		if (b) {
			System.out.println(">>>>>>>>>>>>>> action terminated");
		}
		return b;
	}
	
}
