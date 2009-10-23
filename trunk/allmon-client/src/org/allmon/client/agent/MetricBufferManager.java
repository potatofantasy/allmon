package org.allmon.client.agent;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class is a singleton.<br><br> 
 * 
 * First invocation creates and starts buffering thread.<br><br>
 * 
 * It finishes buffering and flushing when the main (creating) thread ends its live.
 * 
 */
public class MetricBufferManager {

    private static final Log logger = LogFactory.getLog(MetricBufferManager.class);
    
    private final BufferingThread<Long> bufferingThread = new BufferingThread<Long>();
    
    /**
     * Private constructor prevents instantiation from other classes.<br><br>
     * 
     * It starts the buffer process.
     */
    private MetricBufferManager() {
        bufferingThread.start();
    }
    
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final MetricBufferManager INSTANCE = new MetricBufferManager();
    }
    
    public static MetricBufferManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
        
    private class BufferingThread<E> extends Thread {
        
        private ArrayList<E> buffer = new ArrayList<E>();
        private long flushCount = 0;
        
        public void run() {
            logger.debug("run run run and buffer ...");
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                logger.debug("flush buffer ...");
                flush();
            }
        }
        
        public void flush() {
            logger.debug("flushing " + flushCount + " starts...");
            for (int i = 0; i < buffer.size(); i++) {
                logger.debug("> " + buffer.get(i));
            }
            buffer.clear();
            flushCount++;
            logger.debug("end of flushing");
        }
        
        public void add(E e) {
            buffer.add(e);
        }
        
    }
    
    public void add() {
        bufferingThread.add((long)(Math.random() * 1000));
    }
    
}
