package org.allmon.client.agent;

abstract class AgentMetricMessageSender {

    private final Agent agent;
    
    AgentMetricMessageSender(Agent agent) {
        this.agent = agent;
    }
    
    Agent getAgent() {
        return agent;
    }
    
}
