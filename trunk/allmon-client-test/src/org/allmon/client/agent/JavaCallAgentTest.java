package org.allmon.client.agent;

import junit.framework.TestCase;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class JavaCallAgentTest extends TestCase {

	public void testSimple() throws Exception {
		
		MetricMessage message = MetricMessageFactory.createActionClassMessage(
				JavaCallAgentTest.class.getName(), "user", "webSessionId", null);
		
		JavaCallAgent agent = new JavaCallAgent(message);
		agent.entryPoint();
        Thread.sleep(1500);
		agent.exitPoint();
		
		Thread.sleep(10000);
		
	}
	
}
