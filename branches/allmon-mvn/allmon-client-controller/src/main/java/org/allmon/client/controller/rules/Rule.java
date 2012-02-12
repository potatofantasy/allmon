package org.allmon.client.controller.rules;

import java.util.ArrayList;
import java.util.List;

public class Rule {

	private List<Condition> rule = new ArrayList<Condition>();
	
	public boolean add(Condition e) {
		return rule.add(e);
	}

	public int size() {
		return rule.size();
	}

	public List<String> getResources() {
		List<String> resources = new ArrayList<String>();
		for (Condition condition : rule) {
			resources.add(condition.getResource());
		}
		return resources;
	}
	
	public Condition getCondition(int index) {
		return rule.get(index);
	}
	
	public boolean checkIfExceeds(List<State> systemState) {
		int exceedCount = 0;
		// for all conditions in the rule
		for (Condition ruleCondition : rule) {
			String ruleResource = ruleCondition.getResource();
			// check all conditions of the condition state object
			for (State state : systemState) {
				if (ruleResource.equals(state.getResource())) {
					if (ruleCondition.exceeds(state)) {
						exceedCount++;
					}
				}
			}
		}
		// the rule is exceeded only if all rule conditions are exceeded
		if (exceedCount >= size()) {
			return true;
		}
		return false;
	}
	
}