package org.allmon.client.agent;

import java.util.ArrayList;
import java.util.List;

import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class is a buffer implementation for metrics messages. Object of 
 * this class contains collection of metric messages (buffer) and creates a resident 
 * thread which flushes the collection once a time set by flushing interval parameter.<br><br> 
 * 
 * First invocation creates and starts buffering thread.<br><br>
 * 
 * It finishes buffering and flushing when the main (creating) thread ends its live.
 * 
 */
public abstract class AbstractMetricBuffer {

    private static final Log logger = LogFactory.getLog(AbstractMetricBuffer.class);
    
    private final BufferingThread<MetricMessage> bufferingThread = new BufferingThread<MetricMessage>();
    
    /**
     * Constructor starts the buffer process.
     */
    public AbstractMetricBuffer() {
        bufferingThread.start();
    }
    
//    /**
//     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
//     * or the first access to SingletonHolder.INSTANCE, not before.
//     */
//    private static class SingletonHolder {
//        private static final MetricBuffer instance = new MetricBuffer();
//    }
//    
//    public static MetricBuffer getInstance() {
//        return SingletonHolder.instance;
//    }
        
    private class BufferingThread<T> extends Thread {
        
        //private List<T> buffer = Collections.synchronizedList(new ArrayList<T>()); // Slow!
    	private List<T> buffer = new ArrayList<T>();
        private long flushingInterval = 10000;
        
        private long flushCount = 0;
        private long flushedItemsCount = 0;
        private long lastFlushTime = 0;
        private long summaryFlushTime = 0;
        private long lastSendTime = 0;
        private long summarySendTime = 0;
        
        public final void run() {
            logger.debug("run run run and keep buffering ...");
            while (true) {
                try {
                    Thread.sleep(flushingInterval);
                } catch (InterruptedException e) {
                	logger.error(e.getMessage(), e);
                }
                List<T> list = flush();
                send(list);
            }
        }
        
        private List<T> flush() {
        	flushCount++;
            
        	logger.debug("flushing " + flushCount + " starts...");
        	
            List<T> flushingList = new ArrayList<T>();
            
            // coping data to flushing buffer
            long t0 = System.currentTimeMillis(); //System.nanoTime();
        	int copiedCount = 0;
            synchronized (buffer) {
                // because no other than this thread can add or remove items from buffer list,
                // we can check how many items we have in the list and move them to the flushing buffer backwards,
                // so the synchronized block in add method is not needed and other items can be still added to the list
            	
            	//very slow!
            	//Iterator<T> iterator = buffer.iterator();
            	//while(iterator.hasNext()) {
            	//	T t = iterator.next();
            	//	iterator.remove();
	            
            	for (int i = buffer.size() - 1; i >= 0; i--) {
            	    T t = buffer.get(i);
				    buffer.remove(i);
                    flushingList.add(t);
	                copiedCount++;
	                //logger.debug("flushing - removing item from buffer > " + t); // TODO this logging must be deleted
	        	}
            	
            	//Collections.copy(buffer, flushingList);
            	//buffer.clear();
            	//copiedCount = flushingList.size();
            }
            lastFlushTime = System.currentTimeMillis() - t0; //System.nanoTime();
            
        	logger.debug("copied items: " + copiedCount + " in: " + lastFlushTime + "ms, flushingBuffer is ready to send data and clear");
        	
            flushedItemsCount += copiedCount;
            summaryFlushTime += lastFlushTime;
            
            logger.debug("summary of metric messages flushed: " + flushedItemsCount + " items, flushing list is ready to be sent");
        	logger.debug("flushing end.");
        	
        	return flushingList;
        }
        
        /**
         * Sends collected in flushingBuffer data.
         * 
         * TODO If sending operation is to long, reevaluate creating helper 
         * thread to send this data, letting main buffering thread continue.        	
         * 
         * @param flushingList
         */
        public void send(List<T> flushingList) {
        	logger.debug("sending " + flushCount + " starts...");
        	long t0 = System.currentTimeMillis();
        	
        	// call to abstract method which in concrete implements specific sendinf functionality
        	AbstractMetricBuffer.this.sendMetrics(flushingList);
        	
        	flushingList.clear(); // can help with GC
        	
        	lastSendTime = System.currentTimeMillis() - t0;
        	summarySendTime += lastSendTime;
            
        	logger.debug("sending end.");
        }
        
        private void add(T t) {
        	// XXX STOP THE WORLD!
        	// XXX adding is not permitted when flushing method is copying data to flushing buffer ready to send
        	synchronized (buffer) {
        		//logger.debug("adding item to buffer> " + t); // TODO this logging must be deleted
            	buffer.add(t);
        	}
        }
        
    }
    
    /**
     * 
     */
    public abstract void sendMetrics(List flushingList);
    
    /**
     * Add metric message object to buffer ready to send.
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
    
    public long getFlushedItemsCount() {
    	return bufferingThread.flushedItemsCount;
    }
    
    public long getSummaryFlushTime() {
    	return bufferingThread.summaryFlushTime;
    }
    
    /**
     * Sets metrics buffer flushing interval (time between executing flush method).
     * 
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
