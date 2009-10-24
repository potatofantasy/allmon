package org.allmon.client.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.allmon.common.MetricMessage;
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
public class MetricBuffer {

    private static final Log logger = LogFactory.getLog(MetricBuffer.class);
    
    private final BufferingThread<MetricMessage> bufferingThread = new BufferingThread<MetricMessage>();
    
    /**
     * Private constructor prevents instantiation from other classes.<br><br>
     * 
     * It starts the buffer process.
     */
    private MetricBuffer() {
        bufferingThread.start();
    }
    
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final MetricBuffer instance = new MetricBuffer();
    }
    
    public static MetricBuffer getInstance() {
        return SingletonHolder.instance;
    }
        
    private class BufferingThread<T> extends Thread {
        
        //private List<E> buffer = Collections.synchronizedList(new ArrayList<E>()); //new ArrayList<E>();
    	private List<T> buffer = new ArrayList<T>();
        private long flushCount = 0;
        private long flushingInterval = 10000;
        
        public final void run() {
            logger.debug("run run run and buffer ...");
            while (true) {
                try {
                    Thread.sleep(flushingInterval);
                } catch (InterruptedException e) {
                	logger.error(e.getMessage(), e);
                }
                logger.debug("flushing buffer ...");
                flush();
            }
        }
        
        private void flush() {
        	logger.debug("flushing " + (flushCount + 1) + " starts...");
            Iterator<T> iterator = buffer.iterator();
            while(iterator.hasNext()) {
            	synchronized (buffer) {
            		T t = iterator.next();
            		logger.debug("> " + t); // TODO this logging must be deleted
	                iterator.remove();
            	}
            }
            
            flushCount++;
            logger.debug("end of flushing");
        }
        
        private void add(T t) {
        	synchronized (buffer) {
        		buffer.add(t);
        	}
        }
        
    }
    
    /**
     * Add metric object to buffer ready to send.
     */
    public void add(MetricMessage metricMessage) {
        bufferingThread.add(metricMessage);
    }
    
    /**
     * Force flushing the buffer. Must be called before 
     * finishing work with the class object.
     */
    public void flush() {
        bufferingThread.flush();
    }
    
    public long getFlushCount() {
    	return bufferingThread.flushCount;
    }
    
    /**
     * Metric buffer flushing interval  
     * @param flushingInterval
     */
    public void setFlushingInterval(long flushingInterval) {
    	if (flushingInterval <= 0) {
    		throw new RuntimeException("Flushing interfal can not be lower or equel to zero");
    	}
    	bufferingThread.flushingInterval = flushingInterval;
    	if (flushingInterval <= 10) {
    		logger.warn("Flushing interfal is very low, it is set to " + flushingInterval + "ms");
    	}
    }
    
    public long getFlushingInterval() {
    	return bufferingThread.flushingInterval;
    }
    
}
