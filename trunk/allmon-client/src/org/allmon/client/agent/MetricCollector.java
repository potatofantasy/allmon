package org.allmon.client.agent;

import org.allmon.client.scheduler.AgentCallerMain;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetricCollector //extends it.sauronsoftware.cron4j.Task {
                             extends AllmonAgentTask {

    private static final Log logger = LogFactory.getLog(MetricCollector.class);
    
    //public void execute(TaskExecutionContext taskexecutioncontext) throws RuntimeException {
    public void execute() {
        logger.debug(AllmonLoggerConstants.ENTERED);
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage("className", "methodName", "user", 1);
        SimpleMetricMessageSender sender = new SimpleMetricMessageSender(metricMessage);
        sender.insertEntryPoint();
        sender.insertExitPoint();
        logger.debug(AllmonLoggerConstants.EXITED);
    }

//    public static void main(String[] args) {
//        logger.debug(AllmonLoggerConstants.ENTERED);
//        logger.debug("param size : " + args.length);
//        
//        MetricCollector metricCollector = new MetricCollector();
//        metricCollector.execute();
//        
//        logger.debug(AllmonLoggerConstants.EXITED);
//    }
    
}
