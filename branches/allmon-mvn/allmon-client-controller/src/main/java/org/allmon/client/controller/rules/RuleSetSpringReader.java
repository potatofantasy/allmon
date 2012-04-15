package org.allmon.client.controller.rules;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Deprecated
public class RuleSetSpringReader {

	private RuleSet ruleSet = new RuleSet();
	
	public RuleSetSpringReader() {
//		Rule rule1 = new Rule();
//		rule1.add(new Condition("MetricType:OSCPU, Resource:CPU User Time", ">", 0.8));
//		
//		Rule rule2 = new Rule();
//		rule2.add(new Condition("MetricType:OSCPU, Resource:CPU User Time", ">", 0.7));
//		rule2.add(new Condition("MetricType:IO...", ">", 0.5));
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"org/allmon/client/controller/rules/applicationContext-Rules.xml");
		
//		Rule rule1 = (Rule)applicationContext.getBean("rule1"); // ruleList
//		Rule rule2 = (Rule)applicationContext.getBean("rule2"); // ruleList

		List<String> list = (List<String>)applicationContext.getBean("list");
		
		// filling up rule-set
//		ruleSet.add(rule1);
//		ruleSet.add(rule2);
	}

	public static void main(String[] args) {
		RuleSetSpringReader r = new RuleSetSpringReader();
		
	}
	
}
