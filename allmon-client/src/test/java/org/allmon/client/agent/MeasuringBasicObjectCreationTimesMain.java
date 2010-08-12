package org.allmon.client.agent;

import java.util.ArrayList;
import java.util.HashMap;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.client.agent.buffer.AbstractMetricBuffer;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class MeasuringBasicObjectCreationTimesMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int j = 0;
		long l = 0;
		double d = 0;
		ArrayList arrayList = new ArrayList();
		HashMap hashMap = new HashMap();
		MeasuringBasicObjectCreationTimesMain testMain = new MeasuringBasicObjectCreationTimesMain();
		//AgentContext agentContext = new AgentContext();
		
		long t = System.currentTimeMillis();
		
		for (int i = 0; i < 100000000; i++) {
			// 157 for 100M
			//j = i; // 141
			//l = i; // 141
			//m(j); // 140
			//System.currentTimeMillis(); // 141
			//j++; // 219
			//j = i + j; // 219
			//d = i; // 281
			//m(); // 172
			//new Object(); // 1391
			//testMain.new A(); // 2000
			//testMain.new B(); // 2188
			//testMain.new C(); // 2750
			//j = i%2; // 2406
			//arrayList.add(i); arrayList.remove(0); // 6422
			//l = System.currentTimeMillis(); // 6797
			//new ArrayList(); // 7172
			//hashMap.put(j, i); // 8625
			//new HashMap(); // 10093
			//Thread.currentThread().getName(); // 8453
			//Thread.currentThread().toString(); // 76969
			//new MetricMessage(); // 23547, 22922
			//new JavaCallAgent(agentContext, null); // 14380
			//MetricMessageFactory.createClassMessage("", "", "", "", 0); // 89360, 81090
			//new JavaCallAgent(agentContext, MetricMessageFactory.createClassMessage("", "", "", "", 0)); // 97030 
			//System.out.println(); // ?!?!?!? 1244000
		}
		System.out.println("end: " + (System.currentTimeMillis() - t));
	}

	private static final void m() {
	}
	private static final void m(int z) {
		z++;
	}
	
	class A {
	}
	class B extends A {
	}
	class C extends B {
	}
	
}
