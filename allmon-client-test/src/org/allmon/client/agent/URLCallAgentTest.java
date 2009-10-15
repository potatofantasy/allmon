package org.allmon.client.agent;

import junit.framework.TestCase;

public class URLCallAgentTest extends TestCase {

    public void testExecute() {
        URLCallAgent agent = new URLCallAgent();
        agent.setParameters(new String[]{"http://www.google.com", "\\d\\d\\d\\d"});
        agent.execute();
    }
    
}
