package org.allmon.client.controller.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class RuleSetTest {

	private RuleSet ruleSet = new RuleSet();
	
	@Before
	public void setUp() {
		Rule rule1 = new Rule();
		rule1.add(new Condition("CPU", ">", 0.8));
		
		Rule rule2 = new Rule();
		rule2.add(new Condition("CPU", ">", 0.7));
		rule2.add(new Condition("IO", ">", 0.5));
		
		// filling up rule-set
		ruleSet.add(rule1);
		ruleSet.add(rule2);
	}
	
	@Test
	public void test1() {
		assertEquals(2, ruleSet.size());
		assertEquals(2, ruleSet.getResources().length);
	}
	
	@Test
	public void test_ExceedsAll() {
		// all things which have to be collected
		String [] neededResources = ruleSet.getResources();
		
		// current state
		Map<String, State> currentState = new HashMap<String, State>();
		for (String resource : neededResources) {
			// this will be done base on metrics collected
			currentState.put("CPU", new State(resource, 0.9));
		}
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertTrue(b);
	}

	@Test
	public void test_Exceeds1() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.9));
		currentState.put("IO", new State("IO", 0.1));
		currentState.put("MEM", new State("MEM", 0.1));
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertTrue(b);
	}
	
	@Test
	public void test_Exceeds2() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.9));
		currentState.put("IO", new State("IO", 0.7));
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertTrue(b);
	}
	
	@Test
	public void test_Exceeds3() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.75));
		currentState.put("IO", new State("IO", 0.6));
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertTrue(b);
	}
	
	@Test
	public void test_Exceeds4() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.9));
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertTrue(b);
	}
	
	@Test
	public void test_NotExceeds1() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.75));
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertFalse(b);
	}
	
	@Test
	public void test_NotExceeds2() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.1));
		currentState.put("IO", new State("IO", 0.1));
		
		// compare with rule-set and find a matching rule
		boolean b = ruleSet.checkExceededRule(currentState);
		assertFalse(b);
	}
	
}
