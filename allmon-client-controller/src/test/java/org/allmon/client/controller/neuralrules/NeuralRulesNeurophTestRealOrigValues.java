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
public class NeuralRulesNeurophTestRealOrigValues {

	private NeuralRulesNeuroph rules;
	
	@Before
	public void setUp() {
		String action = "se.citerus.dddsample.application.impl.VoidLoadAdderImpl.generateIOLoad";
		Resource [] resources = { 
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 0, 100),  //"CPU",
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 0, 20), // "IO", 
				//new Resource("TS-LAPTOP,192.168.0.10,host=localhost:9999//process:remote,JVMJMX,java.lang:type=Memory:HeapMemoryUsage/used/null", 0, 10000000), //"SLA1", 
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSPROC,Processes Total:/", 0, 1000), //"SLA1", 
				new Resource("TS-LAPTOP,192.168.0.10,monitoring.instance,OSMEM,Mem UsedPercent:/", 0, 100)
				//"TS-LAPTOP,10.1.69.80,monitoring.instance,ACTCLS,ExampleFilter1//HTTP/1.1://127.0.0.1(127.0.0.1):8081//dddsample/public/trackio [null]/127.0.0.1:2082" //"SLA2" 
				};
		
		rules = new NeuralRulesNeuroph(action, resources);
		
		rules.train(INPUT_GOOD, OUTPUT_GOOD, INPUT_BAD, OUTPUT_BAD);
	}

	public static double INPUT_GOOD[][] = { 
		{ 0.20, 3.40, 10, 22, },// 0
		{ 0.12, 2.22, 190, 26, },// 0
		//{ 0.68, 0.94, 333, 40, },// 0
		{ 0.21, 4.63, 170, 20, },// 0
		{ 0.06, 2.40, 10, 10, },// 0
		{ 0.15, 5.60, 50, 10, },// 0
		{ 0.18, 12.2, 100, 88, },// 0
		{ 0.13, 6.22, 10, 10, },// 0
		{ 0.03, 2.27, 10, 10, },// 0
		//{ 0.66, 16.80, 340, 12, },// 0
		{ 0.46, 8.42, 120, 79, },// 1
		{ 0.42, 12.6, 250, 84, },// 1
		{ 0.76, 15.6, 540, 59, },// 1
		{ 0.62, 11.7, 140, 95, },// 1
		{ 0.77, 15.4, 410, 69, },// 1
		{ 0.59, 14.2, 240, 64, },// 1
		{ 0.88, 19.1, 380, 84, },// 1
		//{ 0.28, 18.2, 100, 44, },// 1
	};
	// 1 in output means that the control was applied
	public static double OUTPUT_GOOD[] = 
			//{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 };
			  { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 };
	
	public static double INPUT_BAD[][] = {
			{ 0.25, 15.4, 10, 10 },// 1
			{ 0.28, 2.21, 290, 6 },// 1
			{ 0.31, 16.6, 170, 2 },// 1
			{ 0.15, 6.72, 100, 10 },// 1
			{ 0.17, 11.6, 50, 10 },// 1
			{ 0.12, 16.8, 340, 12 },// 1
			{ 0.77, 19.6, 140, 65},// 0
			{ 0.74, 19.2, 110, 89 },// 0
			{ 0.69, 18.1, 140, 79 },// 0 
			{ 0.47, 10.2, 110, 90 },// 0
			//{ 0.10, 18.3, 100, 44 } // 0
	};
	// 1 in output means that the control was applied
	public static double OUTPUT_BAD[] = //{ 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
										  { 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 };
	
	@Test
	public void testSimpleChecks() throws NeuralRulesException {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSCPU,CPU User Time:/", 0.4897078472429953));
		currentState.put("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 
				new State("TS-LAPTOP,192.168.0.10,monitoring.instance,OSIO,DiskQueue:/C:\\(C:\\)", 15.0));
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
		
		SupervisedTrainingElement element = rules.convertToMLDataSet(currentState);
		double badControlOutput = rules.checkBadControl(element);
		double goodControlOutput = rules.checkGoodControl(element);
		System.out.println("badControlOutput:" + badControlOutput + ", goodControlOutput:" + goodControlOutput);
		
		assertEquals(badControlOutput, 0.1, 0.1);
		assertEquals(goodControlOutput, 0.9, 0.1);
		
		assertTrue(rules.checkRule(currentState));
	}
	
}
