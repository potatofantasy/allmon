package org.allmon.client.agent;

/**
 * This AgentMetricMessageSender is used for all agents.
 * 
 */
abstract class AgentMetricMessageSender {

    private final Agent agent;

    AgentMetricMessageSender(Agent agent) {
        this.agent = agent;
    }

    Agent getAgent() {
        return agent;
    }

}
