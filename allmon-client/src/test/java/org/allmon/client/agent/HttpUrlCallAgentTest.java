package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

    public void testCallWikipedia() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setUrlAddress("http://www.wikipedia.org");
            agent.setSearchPhrase("wikipedia");
            agent.setContentType("text/html");
            //agent.setUrlParameters("-");
            agent.setCheckingHost("www.wikipedia.com");
            agent.setCheckName("www.wikipedia.com-Checker");
            agent.setInstanceName("original-wikipedia-instance");
            agent.setUseProxy(false);
            agent.setRequestMethod("GET");

            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
    
    public void testCallGoogle() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setUrlAddress("http://www.google.com");
            agent.setSearchPhrase("html");
            agent.setContentType("text/html");
            //agent.setUrlParameters("-");
            agent.setCheckingHost("www.google.com");
            agent.setCheckName("www.google.com-Checker");
            agent.setInstanceName("original-google-instance");
            agent.setUseProxy(true);
            agent.setRequestMethod("GET");

            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
    
    public void testCallActiveMQAdmin() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setUrlAddress("http://localhost:8161/admin/");
            agent.setSearchPhrase("Broker");
            agent.setContentType("text/html");
            //agent.setUrlParameters("-");
            agent.setCheckingHost("localhost:8161/admin/");
            agent.setCheckName("ActiveMQAdmin-Checker");
            agent.setInstanceName("original-google-instance");
            agent.setUseProxy(false);
            agent.setRequestMethod("GET");
            
            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
    
    public void testCallActiveMQAdminRsponseTime() throws InterruptedException {
        AgentContext agentContext = new AgentContext();
        try {
            HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContext);
            agent.setStrategy(new HttpUrlCallAgentBooleanStrategy());
            agent.setStrategy(new HttpUrlCallAgentFullResponseTimeStrategy());
            agent.setUrlAddress("http://localhost:8161/admin/");
            agent.setSearchPhrase("Broker");
            agent.setContentType("text/html");
            //agent.setUrlParameters("-");
            agent.setCheckingHost("localhost:8161/admin/");
            agent.setCheckName("ActiveMQAdmin-ResponceTimeChecker");
            agent.setInstanceName("original-google-instance");
            agent.setUseProxy(false);
            agent.setRequestMethod("GET");
            
            agent.execute();
            Thread.sleep(1000);
        } finally {
            agentContext.stop();
        }
    }
        
}