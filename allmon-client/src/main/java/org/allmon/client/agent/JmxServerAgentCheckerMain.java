package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;


public class JmxServerAgentCheckerMain {

    public static void main(String[] args) {
        
        String lvmNamesRegexp = ".*jboss.*"; //args[1]; 
        String mbeansAttributesNamesRegexp = ".*"; //args[2];
        
        System.out.println("lvmNamesRegexp: " + lvmNamesRegexp);
        System.out.println("mbeansAttributesNamesRegexp: " + mbeansAttributesNamesRegexp);
        
        AgentContext agentContext = new AgentContext();
        try {
            JmxServerAgent agent = new JmxServerAgent(agentContext);
//            agent.setParameters(new String[]{lvmNamesRegexp, mbeansAttributesNamesRegexp});// TODO clean code 
            //agent.execute(); // would collect metrics and sent the metrics messages to the broker
//            agent.decodeAgentTaskableParams();
            MetricMessageWrapper messageWrapper = agent.collectMetrics();
            System.out.println(messageWrapper);
        } finally {
            agentContext.stop();
        }
    }

}