package org.allmon.client.agent;

import junit.framework.TestCase;

public class UrlCallAgentTest extends TestCase {

    public void testExecute() {
        UrlCallAgent agent = new UrlCallAgent();
        agent.setParameters(new String[]{
                //"http://www.google.com", 
                "http://www.google.com/#hl=en&q=qwerty",
                "\\d\\d\\d\\d"
                });
        agent.execute();
    }
    
}
