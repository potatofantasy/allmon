package org.allmon.client.agent.java;

import java.io.IOException;

import org.allmon.client.agent.HttpUrlCallAgent;
import org.allmon.client.agent.JavaCallAgent;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;

public class JavaApplicationMain {

    public static void main(String[] args) throws IOException {
        
        for (int i = 0; i < 5; i++) {
            
            System.out.println("Press a button...");
            //System.in.read();
            
            MetricMessage message = MetricMessageFactory.createClassMessage(
                    JavaApplicationMain.class.getName(), "main", "ClassB", "methodB", 1);
            
            JavaCallAgent agent = new JavaCallAgent(message);
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

        // kill buffering thread
        JavaCallAgent.getMetricBuffer().flushAndTerminate();
        
        System.out.println("end.");

        
    }
    
}
