package org.allmon.client.agent;

import org.allmon.common.AbstractLoadTest;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory4Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JavaCallAgentLoadTest extends AbstractLoadTest<Object, JavaCallAgent> {
    
    // c:\jdk1.6.0_04\\bin\java.exe -Xmx512m -Dcom.sun.management.jmxremote 
    public static void main(String[] args) throws InterruptedException {
        JavaCallAgentLoadTest agentLoadTest = new JavaCallAgentLoadTest();
        agentLoadTest.testMain();
    }
    
    private static final Log logger = LogFactory.getLog(JavaCallAgentLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 10;
    private final static long STARTING_TIME_MILLIS = 1 * 1000; // rump-up 1 sec
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 20; // 25ms * 5000x = ~125sec
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 10; //300000; //20000; //10000; //5000;
    // soak test - around 60min
//    private final static long STARTING_TIME_MILLIS = 1 * 60 * 1000; // rump-up 1 min
//    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 20;
//    private final static long SUBSEQUENT_CALLS_IN_THREAD = 360000;
    
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
    
    public JavaCallAgent preCall(int thread, int iteration, Object initParameters) {
        MetricMessage metricMessage = MetricMessageFactory4Test.createClassMessage(
                "className" + iteration, "methodName", "classNameX", "methodNameX", 1);
        metricMessage.setParameters(new String[]{"string1", "string2"});
        
//        MetricMessage metricMessage = MetricMessageFactory4Test.createActionClassMessage(
//                this.getClass().getName(), "user", "webSessionId", null);
        
        JavaCallAgent agent = new JavaCallAgent(agentContext, metricMessage);
        agent.entryPoint();
        return agent;
    }
    
    public void postCall(JavaCallAgent agent) {
        agent.exitPoint();
    }

}