package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

    public void testExecute() {
        HttpUrlCallAgent agent = new HttpUrlCallAgent();
        agent.setParameters(new String[]{
                "http://lontd01/AdminConsole/statuscheck/default.aspx/CheckStatus", 
                "\\w\\w\\w\\w",
                "application/json; charset=utf-8",
                "{ 'componentChecker': 'TTC.iTropics.ComponentCheckers.TropicsDawsComponentChecker, TTC.iTropics.ComponentCheckers' }"
            });
        agent.execute();
    }
    
}