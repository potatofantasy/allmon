package org.allmon.client.agent;

import org.allmon.common.AbstractLoadTest;
import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JavaCallAgentLoadTest extends AbstractLoadTest<Object, JavaCallAgent> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(JavaCallAgentLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 10;
    private final static long STARTING_TIME_MILLIS = 1 * 1000;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 10;
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1000;
    // soak test - around 20min
//    private final static long THREADS_COUNT = 50;
//    private final static long STARTING_TIME_MILLIS = 1 * 60 * 1000; // rump-up 1 min
//    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 1000; // (!) 2 calls per1 sec
//    private final static long SUBSEQUENT_CALLS_IN_THREAD = 1200 * (1000 / SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX);
    
    private final AgentContext agentContext = new AgentContext();
    
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 10000);
        
        //Thread.sleep(5000);
        //agentContext.stop(); // FIXME if uncommented hangs
    }
    
    public Object initialize() {
        return null;
    }
    
    public JavaCallAgent preCall(int thread, int iteration, Object initParameters) {
        MetricMessage metricMessage = MetricMessageFactory4Test.createClassMessage(
                "className" + iteration, "methodName", "classNameX", "methodNameX", 1);
        JavaCallAgent agent = new JavaCallAgent(agentContext, metricMessage);
        agent.entryPoint();
        return agent;
    }
    
    public void postCall(JavaCallAgent agent) {
        agent.exitPoint();

    }
    
    

}