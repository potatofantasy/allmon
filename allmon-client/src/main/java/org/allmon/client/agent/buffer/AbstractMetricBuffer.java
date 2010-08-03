package org.allmon.client.agent.buffer;

import java.util.ArrayList;
import java.util.List;

import org.allmon.common.AllmonCommonConstants;
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
public abstract class AbstractMetricBuffer<M> {

    private static final Log logger = LogFactory.getLog(AbstractMetricBuffer.class);
    
    private boolean verboseLogging = false;
    
    private final BufferingThread<M> bufferingThread = new BufferingThread<M>();
    
    /**
     * Constructor starts the buffer process.
     */
    public AbstractMetricBuffer() {
        bufferingThread.setDaemon(true); // the thread ends when the main thread finishes
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
    
    private class BufferingThread<T extends M> extends Thread {
        
        //private List<T> buffer = Collections.synchronizedList(new ArrayList<T>()); // Slow!
    	private List<T> buffer = new ArrayList<T>();
        private long flushingInterval = 2000;
        
        private long flushCount = 0;
        private long flushedItemsCount = 0;
        private long lastFlushTime = 0;
        private long summaryFlushTime = 0;
        private long lastSendTime = 0;
        private long summarySendTime = 0;
        
        private boolean poisonPill = false;
        
        // maximum time between last call and when thread is deleted
//        private long keepiengTime = 5000; // does it have to be a static field
//        private long lastAddTime = System.currentTimeMillis();
        
        private BufferingThread() {
            setName("BufferingThread-" + getId());
        }
        
        public final void run() {
        	logger.info("Thread is running and keep buffering ...");
        	try {
                //while (!poisonPill && !checkEnd()) { // TODO add a new condition for monitor
                while (!poisonPill) {
                    try {
                        Thread.sleep(flushingInterval);
                    } catch (InterruptedException e) {
                    	logger.error(e.getMessage(), e);
                    }
                    if (!poisonPill) {
                        flushAndSend();
                    }
                }
                // terminating buffering thread
                flushAndSend();
                logger.info("Last sending has finished succesfully - buffer is ending work");
            } catch (Throwable t) {
                logger.error("Error occured in main buffering loop: " + t.getMessage(), t);  
            } finally {
                logger.warn("Run method has been finished - flush method won't be performed anymore");  
            }
        }
        
//        private boolean checkEnd() {
//            return System.currentTimeMillis() - lastAddTime > keepiengTime;
//        }
        
        private void flushAndSend() {
            List<T> list = flush();
            // execute send method only if there is something to send
            if (list.size() > 0) {
                sendFlushingBuffer(list);
            }
        }
        
        private List<T> flush() {
        	flushCount++;
            
        	if (verboseLogging) {
        		logger.debug("Flushing " + flushCount + " starts...");
        	}
        	
            List<T> flushingList = new ArrayList<T>();
            
            // coping data to flushing buffer
            long t0 = System.currentTimeMillis(); //System.nanoTime();
        	int copiedCount = 0;
            synchronized (buffer) {
                // because no other than this thread can add or remove items from buffer list,
                // we can check how many items we have in the list and move them to the flushing buffer backwards,
                // so the synchronised block in add method is not needed and other items can be still added to the list
            	
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
            lastFlushTime = System.currentTimeMillis() - t0;
                        
            flushedItemsCount += copiedCount;
            summaryFlushTime += lastFlushTime;
            
            if (verboseLogging) {
            	logger.debug("Copied items: " + copiedCount + " in: " + lastFlushTime + "ms, flushingBuffer is ready to send data and clear");
                logger.debug("Summary of metric messages flushed: " + flushedItemsCount + " items, flushing list is ready to be sent");
	        	logger.debug("Flushing done");
            }
            
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
        private void sendFlushingBuffer(List<T> flushingList) {
        	if (verboseLogging) {
	        	logger.debug("Sending no: " + flushCount + " starts... - items to send: " + flushingList.size());
        	}
        	
        	long t0 = System.currentTimeMillis();

        	// call to abstract method which in concrete implements specific sending functionality
        	try {
        	    send((List<M>)flushingList);
        	} catch (Throwable t) {
        	    logger.error(t.getMessage(), t);
        	}
        	
        	flushingList.clear(); // can help with GC
        	
        	lastSendTime = System.currentTimeMillis() - t0;
        	summarySendTime += lastSendTime;
            
        	if (verboseLogging) {
        		logger.debug("Sending done");
        	}
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
    public abstract void send(List<M> flushingList);
    
//    public void setKeepiengTime(long keepiengTime) {
//        bufferingThread.keepiengTime = keepiengTime;
//    }
    
    /**
     * Add metric message object to buffer ready to send.
     */
    public void add(M m) {
        bufferingThread.add(m);
    }
    
    /**
     * Force flushing the buffer. Must be called before 
     * finishing work with the class object.
     */
    public void flush() {
        logger.debug("Executing forced flush...");
        bufferingThread.flush();
    }

    /**
     * Force flushing the buffer. Must be called before finishing work with 
     * the class object and finishes work of the buffering thread by sending poison pill.
     * After the pill is sent buffering thread cannot start flushing or sending again.
     * It stops as soon as finishes one of three main procedures (waiting, flushing, sending). 
     */
    public void flushSendTerminate() {
        logger.debug("Forced flush and terminating buffering thread...");
        bufferingThread.flushAndSend();
        bufferingThread.poisonPill = true; // soft way of bufferingThread.interrupt();
        logger.debug("Poison pill has been sent to kill the buffering thread...");
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
    		throw new RuntimeException("Flushing interval can not be lower or equel to zero");
    	}
    	if (flushingInterval < 20) {
    		logger.warn("Flushing interval can not be lower than 20ms, set value: " + flushingInterval + "ms - overridden to 20ms");
    		flushingInterval = 20;
    	}
    	bufferingThread.flushingInterval = flushingInterval;
    	logger.warn("Flushing interval has been set to " + flushingInterval + "ms");
    	if (flushingInterval <= 100) {
    		logger.warn("Flushing interval is very low, every " + flushingInterval + "ms buffer will execute <<send>> method.");
    	}
    }
    
    public long getFlushingInterval() {
    	return bufferingThread.flushingInterval;
    }

	public void setVerboseLogging(boolean verboseLogging) {
		this.verboseLogging = verboseLogging;
		logger.info("Verbose logging has been set to " + verboseLogging);
	}

	public boolean isVerboseLogging() {
		return verboseLogging;
	}

}
