package org.allmon.client.agent;

public class JmxMemoryServerAgent extends JmxServerAgent {

	public JmxMemoryServerAgent(AgentContext agentContext) {
		super(agentContext);
		mbeansAttributesNamesRegexp = "sun.management.Memory";
	}
	
    void decodeAgentTaskableParams() {
        lvmNamesRegexp = getParamsString(0);
    }
    
}
