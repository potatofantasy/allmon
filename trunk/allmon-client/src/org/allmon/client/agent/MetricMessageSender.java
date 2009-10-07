package org.allmon.client.agent;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class is a part of allmon exposed API which can be used in other applications 
 * to acquire metrics in real-time during normal QA, Dev or even Production activity,
 * as a part of passive monitoring approach 
 * (For details see: http://code.google.com/p/allmon/wiki/RnDTheoryPerformanceMonitoring).
 */
public abstract class MetricMessageSender {
    
    private final static Log logger = LogFactory.getLog(MetricMessageSender.class);
    
    private MetricMessage message;
    
    private long startTime = VALUE_NOT_INITIALIZED_LONG;
    private String logId; //VALUE_NOT_INITIALIZED_LONG;
    
    private boolean flagEntryPointWasInserted = false;
    private boolean flagExitPointWasInserted = false;
    
    private final static long VALUE_NOT_INITIALIZED_LONG = -1;

    /**
     * This constructor is accessible only for child concrete classes.
     * 
     * @param className
     * @param methodName
     * @param threadName
     * @param tropicsUser
     * @param webSessionId
     */
    public MetricMessageSender(MetricMessage message) {
        this.message = message;
    }
    
    //protected abstract boolean isRTLoggingEnabled();
    private boolean enabled = true;
    protected boolean isEnabled() {
        return enabled;
    }
    
    public abstract void insertEntryPoint();
    
    public abstract void insertExitPoint();
    
    public abstract void insertExitPointException(Exception exception);
    
    
    protected final void sendEntryPoint() {
        String methodName = "sendEntryPoint";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName + AllmonLoggerConstants.ENTERED);
        }
        
        if (flagEntryPointWasInserted) {
            logger.error(methodName + " entry point cannot be inserted because the entry point has been already inserted");
            return;
        }
        
        startTime = System.currentTimeMillis();
        
        logId = "log log log"; //TODO MetricMessageFactory.generateLogId(startTime);
        
        if (isEnabled()) {
            // send a message
            MessageSender messageSender = new MessageSender();
            message.setPoint(AllmonCommonConstants.METRIC_POINT_ENTRY);
            messageSender.sendMessage(message);
        }
        
        if (logger.isDebugEnabled()) {
             logger.debug(methodName + AllmonLoggerConstants.EXITED);
        }
    }

    // XXX 
    protected final void sendExitPoint(Exception exception) {
        String methodName = "sendExitPoint";
        // add exit point only if insertActionClassEntryPointForRTMonitoring was called successfully before
        if (startTime != VALUE_NOT_INITIALIZED_LONG && !"".equals(logId)) {
            if (!flagExitPointWasInserted) {
                sendExitPoint(System.currentTimeMillis() - startTime, exception);
            } else {
//                logger.error(methodName + " exit point cannot be inserted because the exit point has been already inserted");
            }
        } else {
//            logger.error(methodName + " exit point cannot be inserted because either entry method was not called properly or logId param wasn't initialized correctly");
        }
    }
    
    private final void sendExitPoint(long executionTimeMS, Exception exception) {
        String methodName = "sendExitPoint";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName + AllmonLoggerConstants.ENTERED);
        }
        
        if (isEnabled()) {
            // send a message
            MessageSender messageSender = new MessageSender();
            //messageSender.sendTextMessage(generateMessage() + "-" + executionTimeMS + "-" + exceptionText);
            //messageSender.sendTextMessage("generateMessage()" + "-" + executionTimeMS + "-" + exceptionText); // TODO generateMessage()
            message.setDurationTime(executionTimeMS);
            message.setException(exception);
            message.setPoint(AllmonCommonConstants.METRIC_POINT_EXIT);
            messageSender.sendMessage(message);
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug(methodName + AllmonLoggerConstants.EXITED);
        }     
    }
    
}
