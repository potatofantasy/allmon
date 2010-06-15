package org.allmon.client.agent;

import org.allmon.common.AbstractLoadTest;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SnmpHostAgentLoadTest extends AbstractLoadTest<Object, SnmpHostAgent> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(SnmpHostAgentLoadTest.class);
    
    
    // stress test
    private final static long THREADS_COUNT = 30;
    private final static long STARTING_TIME_MILLIS = 10; // rump-up
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 0; // 25ms * 5000x = ~125sec
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 10;
    
    private final AgentContext agentContext = new AgentContext();
        
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 10000);
        
        agentContext.stop();
    }
    
    public Object initialize() {
        return null;
    }

    public SnmpHostAgent preCall(int thread, int iteration, Object initParameters) {
        SnmpHostAgent agent = new SnmpHostAgent(agentContext);
//        agent.setParameters(new String[]{"10.1.132.99"}); // FIXME clean code
        agent.execute();
        return agent;
    }
    
    public void postCall(SnmpHostAgent preCallParameters) {
    }

}