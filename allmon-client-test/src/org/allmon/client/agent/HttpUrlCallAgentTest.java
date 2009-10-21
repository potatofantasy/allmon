package org.allmon.client.agent;

import junit.framework.TestCase;

public class HttpUrlCallAgentTest extends TestCase {

    public void testExecute() {
        HttpUrlCallAgent agent = new HttpUrlCallAgent();
        agent.setParameters(new String[]{
                //"http://www.google.com/#hl=en&q=qwerty",
                "http://lontd01/AdminConsole/statuscheck/default.aspx/CheckStatus", 
                "\\d\\d\\d\\d",
                //"text/html",
                //"application/x-www-form-urlencoded",
                "application/json; charset=utf-8",
                //""
                "{ 'componentChecker': 'TTC.iTropics.ComponentCheckers.TropicsDawsComponentChecker, TTC.iTropics.ComponentCheckers' }"
            });
        agent.execute();
    }
    
}