package org.allmon.client.agent;

import org.allmon.common.MessageSender;
import org.allmon.common.MetricMessage;
//import org.apache.log4j.Logger;

public abstract class MetricMessageSender {
    
    //private final static Logger logger = Logger.getLogger(MetricMessageSender.class);
    
    private MetricMessage message; //TODO //MetricMessage metricMessage = MetricMessageFactory.createActionClassMessage(className, tropicsUser, webSessionId, request, durationTime));
    
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
    
    protected abstract boolean isRTLoggingEnabled();
    
    public abstract void insertEntryPoint();
    
    public abstract void insertExitPoint();
    
    public abstract void insertExitPointException(String exceptionText);
    
    
    protected final void sendEntryPoint() {
        String methodName = "sendEntryPoint";
        
        if (flagEntryPointWasInserted) {
            //logger.error(methodName + " entry point cannot be inserted because the entry point has been already inserted");
            return;
        }
        
//        if (logger.isDebugEnabled()) {
//            logger.debug(methodName + TropicsConstants.LOGGER_ENTERED);
//        }
        
        startTime = System.currentTimeMillis();
        
        logId = "log log log"; //TODO MetricMessageFactory.generateLogId(startTime);
        
        if (isRTLoggingEnabled()) {
            // send a message
            MessageSender messageSender = new MessageSender();
            messageSender.sendMessage(message);
        }
        
//        if (logger.isDebugEnabled()) {
//             logger.debug(methodName + TropicsConstants.LOGGER_EXITED);
//        }
    }

    // TODO !!!! add source of the exception (what class is throwing the exception)
    // XXX 
    // XXX 
    // XXX 
    // XXX 
    protected final void sendExitPoint(String exceptionText) {
        String methodName = "sendExitPoint";
        // add exit point only if insertActionClassEntryPointForRTMonitoring was called successfully before
        if (startTime != VALUE_NOT_INITIALIZED_LONG && !"".equals(logId)) {
            if (!flagExitPointWasInserted) {
                sendExitPoint(System.currentTimeMillis() - startTime, exceptionText);
            } else {
//                logger.error(methodName + " exit point cannot be inserted because the exit point has been already inserted");
            }
        } else {
//            logger.error(methodName + " exit point cannot be inserted because either entry method was not called properly or logId param wasn't initialized correctly");
        }
    }
    
    private final void sendExitPoint(long executionTimeMS, String exceptionText) {
        String methodName = "sendExitPoint";
//        if (logger.isDebugEnabled()) {
//            logger.debug(methodName + TropicsConstants.LOGGER_ENTERED);
//        }
        
        if (isRTLoggingEnabled()) {
            // send a message
            MessageSender messageSender = new MessageSender();
            //messageSender.sendTextMessage(generateMessage() + "-" + executionTimeMS + "-" + exceptionText);
            //messageSender.sendTextMessage("generateMessage()" + "-" + executionTimeMS + "-" + exceptionText); // TODO generateMessage()
            message.setDurationTime(executionTimeMS);
            message.setException(exceptionText);
            messageSender.sendMessage(message);
        }
        
//        if (logger.isDebugEnabled()) {
//            logger.debug(methodName + TropicsConstants.LOGGER_EXITED);
//        }       
    }
    
}
