package org.allmon.client.agent;

import junit.framework.TestCase;

public class URLCallAgentTest extends TestCase {

    public void testExecute() {
        UrlCallAgent agent = new UrlCallAgent();
        agent.setParameters(new String[]{"http://www.google.com", "\\d\\d\\d\\d"});
        agent.execute();
    }
    
}
