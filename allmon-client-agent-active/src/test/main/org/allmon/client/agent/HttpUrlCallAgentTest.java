package org.allmon.client.agent;


import org.easymock.classextension.EasyMock;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

	private AgentContext agentContextMock; 
	
	protected void setUp() throws Exception {
		//AgentContext agentContext = new AgentContext();
    	agentContextMock = EasyMock.createMock(AgentContext.class);
    	AgentMetricBuffer agentMetricBufferMock = EasyMock.createMock(AgentMetricBuffer.class);
    	EasyMock.expect(agentContextMock.getMetricBuffer()).andReturn(agentMetricBufferMock);
    	//EasyMock.expect(agentContextMock.stop());
    	//EasyMock.expectLastCall();
    	EasyMock.replay(agentContextMock);
	}
	
    public void testCallWikipedia() throws InterruptedException {
	    HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContextMock);
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
    }
    
    public void testCallGoogle() throws InterruptedException {
    	HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContextMock);
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
    }
    
    public void testCallActiveMQAdmin() throws InterruptedException {
    	HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContextMock);
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
    }
    
    public void testCallActiveMQAdminRsponseTime() throws InterruptedException {
    	HttpUrlCallAgent agent = new HttpUrlCallAgent(agentContextMock);
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
    }
        
}