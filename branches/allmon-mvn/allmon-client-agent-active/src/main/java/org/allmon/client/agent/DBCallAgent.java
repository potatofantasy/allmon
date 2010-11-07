package org.allmon.client.agent;

import org.allmon.common.MetricMessageWrapper;

/**
 * This agent can be used to poll RDBMS statistics and other applications metrics 
 * stored in database.
 * 
 * 1. connects to database specified by connectionString parameter
 * 2. execute the sqls on database
 * 3. extract metrics data from resultset and return metrics message collection
 * 4. disconnects and sends metrics to the broker
 * 
 * SQLs can be either selects or delete statements only.
 * 
 * Selects have to have defined 3 or 4 columns, first column always contains row unique id,
 * second numeric values which are metrics, third represents resource and optional 
 * fourth column contains source attribute.
 * 
 * This agent can be used for poling tables used as logs. To do so, metrics data 
 * have to be selected first and later deleted. Delete statement takes first column data
 * (row unique id) and delete this rows from specified table. 
 *
 */
public class DBCallAgent extends ActiveAgent {

	private String connectionString;
	private String[] sqls;

	public DBCallAgent(AgentContext agentContext) {
		super(agentContext);
	}
	
	MetricMessageWrapper collectMetrics() {
		// TODO execute the sqls on database specified by connectionString
		// TODO extract metrics data from resultset and return metrics message collection
		
		connect();
		
		MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
		for (String sql : sqls) {
			metricMessageWrapper.add(executeSql());
		}
		
		return metricMessageWrapper;
	}

	private void connect() {
		
	}
	
	private MetricMessageWrapper executeSql(){
		return new MetricMessageWrapper();
	}
	
//    void decodeAgentTaskableParams() {
//    	connectionString = getParamsString(0);
//    	// TODO add parameter setting mechanism for Strings collection
//    	sqls = new String[10];
//    	sqls[0] = getParamsString(1);
//    	sqls[1] = getParamsString(2);
//    	/// ...
//    }

}
