package org.allmon.client.agent;

public class JmxGcServerAgent extends JmxServerAgent {

	public JmxGcServerAgent(AgentContext agentContext) {
		super(agentContext);
		mbeansAttributesNamesRegexp = "sun.management.GarbageCollector";
	}
	
    void decodeAgentTaskableParams() {
        lvmNamesRegexp = getParamsString(0);
    }
    
}
