package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;

public class DBCallAgent extends ActiveAgent {

	private String connectionString;
	private String sqlQuery;

	public DBCallAgent(AgentContext agentContext) {
		super(agentContext);
	}
	
	MetricMessageWrapper collectMetrics() {
		// TODO execute the sqlQuery on database specified by sqlQuery
		// TODO extract metrics data from resultset and return metrics message
		return null;
	}

    void decodeAgentTaskableParams() {
    	connectionString = getParamsString(0);
    	sqlQuery = getParamsString(1);
    }

}
