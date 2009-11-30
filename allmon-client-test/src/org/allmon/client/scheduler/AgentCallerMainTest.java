package org.allmon.client.scheduler;

import junit.framework.TestCase;

public class AgentCallerMainTest extends TestCase {

    public void test4LocalBrokerCheck() throws Exception {
        String [] args = {
            org.allmon.client.agent.HttpUrlCallAgent.class.getName(), 
            "10", // 10 seconds wait
            org.allmon.client.agent.HttpUrlCallAgentFullResponseTimeStrategyTest.class.getName(), // setStrategy
            "http://localhost:8161/admin/", //urlAddress
            "Broker", // searchPhrase
            "text/html", // contentType
            "", // urlParameters
            "localhost", // checkingHost
            "local-broker-check", // checkName
            "local-broker", // instanceName
            "false", // useProxy
            "POST" // requestMethod
            };
        
        AgentCallerMain.main(args);
    }

}