package org.allmon.client.controller.terminator;

import java.util.HashMap;
import java.util.Map;

import org.allmon.client.controller.neuralrules.NeuralRulesNeuroph;
import org.allmon.client.controller.neuralrules.NeuralRulesReceiver;
import org.allmon.client.controller.neuralrules.Resource;
import org.allmon.client.controller.rules.State;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * termination based on Neural Network Rules definition
 * 
 */
public class NeuralRulesJavaCallTerminatorController extends AbstractAllmonMetricsJavaCallTerminatorController {

	@Override
	public boolean terminate(ProceedingJoinPoint call) {
		String className = call.getSignature().getDeclaringTypeName();
		String methodName = call.getSignature().getName();
		Object [] arguments = call.getArgs();
		
		System.out.println("allmonMetricsReceiver.metricsDataStore.keysCount()/valuesCount(): " + 
				allmonMetricsReceiver.metricsDataStore.keysCount() + 
				"/" + allmonMetricsReceiver.metricsDataStore.valuesCount());
//		System.out.println("allmonMetricsReceiver.metricsDataStore.keysSet(): " + 
//				allmonMetricsReceiver.metricsDataStore.keysSet());
		
		return terminateNN(className + "." + methodName);
	}
	
	// receiver instance 
	protected static final NeuralRulesReceiver neuralRulesReceiver = new NeuralRulesReceiver();
	
	///////////////////////////
	// This object will be amended and synchronized by a remote analyzer/evaluator process
	// TODO move to Action - introduce Action and Resource classes
	public static final Map<String, NeuralRulesNeuroph> neuralRulesMap = new HashMap<String, NeuralRulesNeuroph>();
	
	public boolean terminateNN(String action) {
		System.out.println("terminateNN - className:" + action); 
				
		NeuralRulesNeuroph rules = neuralRulesMap.get(action);
		if (rules == null) {
			System.out.println("terminateNN - no neural rules in controller process - no control decission can be takes"); 
			return false;
		}
		
		Resource[] resources = rules.getResources();
		Map<String, State> currentState = createSystemState(
				allmonMetricsReceiver.metricsDataStore, resources, 60000);
		// checking termination condition
		boolean b = rules.checkRule(currentState);
		if (b) {
			System.out.println(">>>>>>>>>>>>>> action terminated");
		}
		return b;
	}
	
}
