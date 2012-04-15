package org.allmon.client.controller.neuralrules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.allmon.client.controller.rules.State;
import org.junit.Before;
import org.junit.Test;
import org.neuroph.core.learning.SupervisedTrainingElement;

public class NeuralRulesNeurophTest {

	private NeuralRulesNeuroph rules;
	
	@Before
	public void setUp() {
		String action = "Action.action";
		Resource [] resources = {
				new Resource("CPU", 0, 1), 
				new Resource("IO", 0, 1), 
				new Resource("SLA1", 0, 1), 
				new Resource("SLA2", 0, 1)};
		
		rules = new NeuralRulesNeuroph(action, resources);
		
		rules.train(INPUT_GOOD, OUTPUT_GOOD, INPUT_BAD, OUTPUT_BAD);
	}
	
	@Test
	public void test() {
		assertNotNull(rules);
	}

	public static double INPUT_GOOD[][] = { 
		{0.20, 0.17, 0.01, 0,},//    0
		{0.32, 0.81, 0.19, 0.06,},// 0
		{0.68, 0.94, 0.33, 0.04,},// 0
		{0.31, 0.83, 0.17, 0.02,},// 0
		{0.06, 0.42, 0.01, 0,},//    0
		{0.15, 0.58, 0.05, 0,},//    0
		{0.66, 0.84, 0.34, 0.12,},// 0
		{0.75, 0.92, 0.44, 0.29,},// 1
		{0.72, 0.93, 0.55, 0.24,},// 1
		{0.76, 0.98, 0.54, 0.29,},// 1
		{0.62, 0.98, 0.54, 0.15,},// 1
		{0.77, 0.97, 0.41, 0.29,},// 1
		{0.59, 0.91, 0.24, 0.14,},// 1
		{0.68, 0.95, 0.38, 0.24,},// 1
		{0.28, 0.91, 0.10, 0.44,},// 1
		{0.18, 0.61, 0.10, 0.88,},// 0
		{0.13, 0.31, 0.0,  0.0,},//  0
		{0.03, 0.11, 0.0,  0.0,},//  0
	};
	// 1 in output means that the control was applied 
	public static double OUTPUT_GOOD[] = {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0};
	
	public static double INPUT_BAD[][] = { 	
		{0.25, 0.77, 0.01, 0},//    1   //{0.25, 0.17, 0.01, 0},//    1
		{0.38, 0.81, 0.29, 0.06},// 1
		{0.31, 0.83, 0.17, 0.02},// 1
		{0.15, 0.42, 0.01, 0},//    1
		{0.17, 0.58, 0.05, 0},//    1
		{0.62, 0.84, 0.34, 0.12},// 1
		//{0.77, 0.98, 0.54, 0.15},// 0
		{0.74, 0.97, 0.41, 0.29},// 0
		{0.69, 0.91, 0.24, 0.14},// 0
		{0.63, 0.95, 0.38, 0.24},// 0
		{0.10, 0.91, 0.10, 0.44}// 0
	};
	// 1 in output means that the control was applied 
	public static double OUTPUT_BAD[] = {1, 1, 1, 1, 1, 1, 0, 0, 0, 0};

	
	@Test
	public void testSimpleChecks() throws NeuralRulesException {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.7));
		currentState.put("IO", new State("IO", 0.9));
		currentState.put("SLA1", new State("SLA1", 0.6)); // .5
		currentState.put("SLA2", new State("SLA2", 0.3)); // .3
		
		SupervisedTrainingElement element = rules.convertToMLDataSet(currentState);
		double badControlOutput = rules.checkBadControl(element);
		double goodControlOutput = rules.checkGoodControl(element);
		System.out.println("badControlOutput:" + badControlOutput + ", goodControlOutput:" + goodControlOutput);
		
		assertEquals(badControlOutput, 0.1, 0.1);
		assertEquals(goodControlOutput, 0.9, 0.1);
		
		assertTrue(rules.checkRule(currentState));
	}
	
	@Test
	public void testMainCheck() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.7));
		currentState.put("IO", new State("IO", 0.9));
		currentState.put("SLA1", new State("SLA1", 0.6)); // .5
		currentState.put("SLA2", new State("SLA2", 0.3)); // .3
		assertTrue(rules.checkRule(currentState));
		
		Map<String, State> currentState2 = new HashMap<String, State>();
		currentState2.put("CPU", new State("CPU", 0.9));
		currentState2.put("IO", new State("IO", 0.9));
		currentState2.put("SLA1", new State("SLA1", 0.5)); // .5
		currentState2.put("SLA2", new State("SLA2", 0.3)); // .3
		assertTrue(rules.checkRule(currentState2));

		Map<String, State> currentState3 = new HashMap<String, State>();
		currentState3.put("CPU", new State("CPU", 0.6));
		currentState3.put("IO", new State("IO", 0.8));
		currentState3.put("SLA1", new State("SLA1", 0.3)); // .5
		currentState3.put("SLA2", new State("SLA2", 0.1)); // .3
		assertFalse(rules.checkRule(currentState3));
	}
	
	@Test
	public void testMainCheck_NNResourceNotInCurrentState() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU_XXX", new State("CPU", 0.7)); // !!!
		currentState.put("IO", new State("IO", 0.9));
		currentState.put("SLA1", new State("SLA1", 0.6));
		currentState.put("SLA2", new State("SLA2", 0.3));
		assertFalse(rules.checkRule(currentState)); // no termination
	}

	@Test
	public void testSerialization() {
		NeuralRulesNeuroph rulesLocal = 
			NeuralRulesNeuroph.instantiateSerialized(rules.serialize());
		
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.7));
		currentState.put("IO", new State("IO", 0.9));
		currentState.put("SLA1", new State("SLA1", 0.6));
		currentState.put("SLA2", new State("SLA2", 0.3));
		assertTrue(rulesLocal.checkRule(currentState));
	}

}
