package org.allmon.client.controller.rules;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ConditionTest {

	@Test(expected=RuntimeException.class)
	public void exceeds_NotSameResource() {
		Condition c1 = new Condition("CPU", ">", 0.8);
		State state = new State("IO", 0.9);
		assertTrue(c1.exceeds(state));
	}
	
	@Test
	public void exceeds_Greater() {
		Condition c1 = new Condition("CPU", ">", 0.8);
		State state1 = new State("CPU", 0.9);
		assertTrue(c1.exceeds(state1));

		Condition c2 = new Condition("CPU", ">", 0.8);
		State state2 = new State("CPU", 0.8);
		assertFalse(c2.exceeds(state2));

		Condition c3 = new Condition("CPU", ">", 0.8);
		State state3 = new State("CPU", 0.5);
		assertFalse(c3.exceeds(state3));
	}
	
	@Test
	public void exceeds_Lesser() {
		Condition c1 = new Condition("CPU", "<", 0.8);
		State state1 = new State("CPU", 0.7);
		assertTrue(c1.exceeds(state1));
		
		Condition c2 = new Condition("CPU", "<", 0.8);
		State state2 = new State("CPU", 0.8);
		assertFalse(c2.exceeds(state2));

		Condition c3 = new Condition("CPU", "<", 0.8);
		State state3 = new State("CPU", 0.9);
		assertFalse(c3.exceeds(state3));
	}
		
}
