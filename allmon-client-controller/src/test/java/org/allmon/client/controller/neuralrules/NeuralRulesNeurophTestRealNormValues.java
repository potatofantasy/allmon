package org.allmon.client.controller.neuralrules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.allmon.client.controller.rules.State;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.learning.SupervisedTrainingElement;

/**
 * Real life scenario 
 * - real metrics, resources and values
 * - testing denormalization; real(denormalized) values against trained in NN normalized <0,1> values
 */
public class NeuralRulesNeurophTestRealNormValues {

	public static void main(String[] args) {
		for (int i = 0; true; i++) {			
		}
	}
	
	private NeuralRulesNeuroph rules;
	
	@Before
	public void setUp() {
		String action = "se.citerus.dddsample.application.impl.VoidLoadAdderImpl.generateIOLoad";
		Resource [] resources = { 
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/"),  //"CPU",
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)"), // "IO", 
				//new Resource("TS-LAPTOP,192.168.0.10,host=localhost:9999//process:remote,JVMJMX,java.lang:type=Memory:HeapMemoryUsage/used/null", 0, 10000000), //"SLA1", 
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/"), //"SLA1", 
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/")
				//"TS-LAPTOP,10.1.69.80,monitoring.instance,ACTCLS,ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]/127.0.0.1:2082" //"SLA2" 
				};
		
		rules = new NeuralRulesNeuroph(action, resources);
		
		rules.train(INPUT_GOOD, OUTPUT_GOOD, INPUT_BAD, OUTPUT_BAD);
	}

	public static double INPUT_GOOD[][] = { 
			{ 0.20, 0.17, 0.01, 0.22, },// 0
			{ 0.12, 0.11, 0.19, 0.26, },// 0
			//{ 0.68, 0.94, 0.33, 0.04, },// 0
			{ 0.21, 0.23, 0.17, 0.02, },// 0
			{ 0.06, 0.12, 0.01, 0, },// 0
			{ 0.15, 0.28, 0.05, 0, },// 0
			//{ 0.66, 0.84, 0.34, 0.12, },// 0
			{ 0.46, 0.42, 0.12, 0.79, },// 1 -- !!!
			{ 0.42, 0.63, 0.25, 0.84, },// 1
			{ 0.76, 0.78, 0.54, 0.59, },// 1
			{ 0.62, 0.58, 0.14, 0.95, },// 1
			{ 0.77, 0.77, 0.41, 0.69, },// 1
			{ 0.59, 0.71, 0.24, 0.64, },// 1
			{ 0.88, 0.95, 0.38, 0.84, },// 1
			//{ 0.28, 0.91, 0.10, 0.44, },// 1
			{ 0.18, 0.61, 0.10, 0.88, },// 0
			{ 0.13, 0.31, 0.0, 0.0, },// 0
			{ 0.03, 0.11, 0.0, 0.0, },// 0
	};
	// 1 in output means that the control was applied
	public static double OUTPUT_GOOD[] = 
			//{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 };
			  { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 };
	
	public static double INPUT_BAD[][] = { 
			{ 0.25, 0.77, 0.01, 0 },// 1
			{ 0.28, 0.11, 0.29, 0.06 },// 1
			{ 0.31, 0.83, 0.17, 0.02 },// 1
			{ 0.15, 0.42, 0.01, 0 },// 1
			{ 0.17, 0.58, 0.05, 0 },// 1
			{ 0.12, 0.84, 0.34, 0.12 },// 1 --- !!!
			{ 0.77, 0.98, 0.14, 0.65},// 0
			{ 0.74, 0.97, 0.31, 0.89 },// 0
			{ 0.69, 0.91, 0.14, 0.79 },// 0 
			{ 0.47, 0.51, 0.11, 0.89 },// 0
			//{ 0.10, 0.91, 0.10, 0.44 } // 0
	};
	// 1 in output means that the control was applied
	public static double OUTPUT_BAD[] = //{ 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 };
										  { 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 };

	
	@Test
	public void testSimpleChecks() throws NeuralRulesException {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 0.4897078472429953));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 5.0));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/", 106.0));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/", 89.37329575118618));
		
		SupervisedTrainingElement element = rules.convertToMLDataSet(currentState);
		double badControlOutput = rules.checkBadControl(element);
		double goodControlOutput = rules.checkGoodControl(element);
		System.out.println("badControlOutput:" + badControlOutput + ", goodControlOutput:" + goodControlOutput);
		
		assertEquals(badControlOutput, 0.1, 0.1);
		assertEquals(goodControlOutput, 0.9, 0.1);
		
		assertTrue(rules.checkRule(currentState));
	}
	
	@Test
	public void testSimpleChecks_HighIO() throws NeuralRulesException {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 0.4897078472429953));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 9.0));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/", 106.0));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/", 89.37329575118618));
		
		//TSIKORA-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/=, 
		//TSIKORA-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\(C:\)=, 
		//TSIKORA-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/=, 
		//TSIKORA-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/=
		
		SupervisedTrainingElement element = rules.convertToMLDataSet(currentState);
		double badControlOutput = rules.checkBadControl(element);
		double goodControlOutput = rules.checkGoodControl(element);
		System.out.println("badControlOutput:" + badControlOutput + ", goodControlOutput:" + goodControlOutput);
		
		assertEquals(badControlOutput, 0.1, 0.1);
		assertEquals(goodControlOutput, 0.9, 0.1);
		
		assertTrue(rules.checkRule(currentState));
	}
	
}
