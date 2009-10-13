package org.allmon.client.agent;

import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.MetricMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 *
 */
public class SimpleMetricMessageAgent extends ActiveAgent  {

    private static final Log logger = LogFactory.getLog(SimpleMetricMessageAgent.class);
    
    public void execute() {
        logger.debug(AllmonLoggerConstants.ENTERED);
        MetricMessage metricMessage = MetricMessageFactory.createClassMessage("className", "methodName", "user", 1);
        SimpleMetricMessageSender sender = new SimpleMetricMessageSender(metricMessage);
        sender.insertEntryPoint();
        sender.insertExitPoint();
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
