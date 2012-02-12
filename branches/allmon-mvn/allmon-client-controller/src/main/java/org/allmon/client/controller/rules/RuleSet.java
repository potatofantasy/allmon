package org.allmon.client.controller.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleSet {

	private List<Rule> rulesList = new ArrayList<Rule>();
	private Set<String> resourcesUsed = new HashSet<String>();

	public boolean add(Rule r) {
		resourcesUsed.addAll(r.getResources());
		return rulesList.add(r);
	}

	public int size() {
		return rulesList.size();
	}
	
	public String[] getResources() {
		return resourcesUsed.toArray(new String[]{});
	}

	public boolean checkExceededRule(List<State> systemState) {
		// search all rules in the rules-set and if a
		for (Rule rule : rulesList) {
			if (rule.checkIfExceeds(systemState)) {
				return true;
			}
		}
		return false;
	}
}