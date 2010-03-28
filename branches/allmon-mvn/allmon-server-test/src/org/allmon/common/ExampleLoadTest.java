package org.allmon.common;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExampleLoadTest extends AbstractLoadTest<ArrayList<String>, Object> {
    
    private static final Log logger = LogFactory.getLog(ExampleLoadTest.class);
    
    // stress test
    private final static long THREADS_COUNT = 40;
    private final static long STARTING_TIME_MILLIS = 1;
    private final static long SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX = 10; // no sleep
    private final static long SUBSEQUENT_CALLS_IN_THREAD = 100;
    
    private final ArrayList<String> buffer = new ArrayList<String>();
    
    public void testMain() throws InterruptedException {
        runLoadTest(THREADS_COUNT, 
                STARTING_TIME_MILLIS, SUBSEQUENT_CALLS_IN_THREAD_SLEEP_MAX, 
                SUBSEQUENT_CALLS_IN_THREAD, 3000);
        
        for (int i = 0; i < buffer.size(); i++) {
            System.out.print(buffer.get(i) + ", ");
        }
        System.out.println();
        System.out.println(buffer.toString());
        System.out.println("End.");
    }
    
    public ArrayList<String> initialize() {
        return buffer;
    }
    
    public Object preCall(int thread, int iteration, ArrayList<String> buffer) {
        buffer.add(" string:" + thread + ":" + iteration);
        System.out.println(" string:" + thread + ":" + iteration);
        return null;
    }
    
    public void postCall(Object preCallParameters) {
    }
    
}