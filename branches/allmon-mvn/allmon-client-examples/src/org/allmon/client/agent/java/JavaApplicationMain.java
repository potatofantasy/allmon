package org.allmon.client.agent.java;

import java.io.IOException;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class JavaApplicationMain {

    public static void main(String[] args) throws IOException {
    	AgentContext agentContext = new AgentContext();
    	try {
	    	for (int i = 0; i < 5; i++) {
	            System.out.println("Press a button...");
	            //System.in.read();
	            
	            MetricMessage message = MetricMessageFactory.createClassMessage(
	                    JavaApplicationMain.class.getName(), "main", "ClassB", "methodB", 1);
	            
	            JavaCallAgent agent = new JavaCallAgent(agentContext, message);
	            agent.entryPoint();
	            
	            // emulating some computations
	            try {
	                Thread.sleep((long)(Math.random() * 1000));
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            } finally {
	                agent.exitPoint();
	            }
	        }
	        System.out.println("Five messages after users interacins have been sent.");
    	} finally {
	        // kill buffering thread and close connections to broker
	        agentContext.stop();
    	}
        System.out.println("end.");
        
    }
    
}
