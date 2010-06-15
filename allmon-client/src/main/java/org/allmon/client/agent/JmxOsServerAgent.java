package org.allmon.client.agent;

public class JmxOsServerAgent extends JmxServerAgent {

	public JmxOsServerAgent(AgentContext agentContext) {
		super(agentContext);
		mbeansAttributesNamesRegexp = "sun.management.OperatingSystem";
	}
	
//    void decodeAgentTaskableParams() {
//        lvmNamesRegexp = getParamsString(0);
//    }
    
}
