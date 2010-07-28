package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JdbcAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(JdbcAgent.class);
    
    public JdbcAgent(AgentContext agentContext) {
		super(agentContext);
	}

	public final MetricMessageWrapper collectMetrics() {
	    return null;
	}

}
