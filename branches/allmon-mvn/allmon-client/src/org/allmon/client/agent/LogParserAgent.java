package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;

public class LogParserAgent extends ActiveAgent {

    public LogParserAgent(AgentContext agentContext) {
		super(agentContext);
	}

	MetricMessageWrapper collectMetrics() {
		// TODO Parse a file or a list of files
		// TODO It necessary to store on client information where previous collecting process finished
		
		return null;
	}

    void decodeAgentTaskableParams() {
    }

}
