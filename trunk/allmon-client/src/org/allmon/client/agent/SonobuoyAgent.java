package org.allmon.client.agent;

import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agents sends ping messages (signals) only.
 */
public class SonobuoyAgent extends ActiveAgent  {

    private static final Log logger = LogFactory.getLog(SonobuoyAgent.class);
    
    public void execute() {
        logger.debug(AllmonLoggerConstants.ENTERED);
        MetricMessage metricMessage = MetricMessageFactory.createPingMessage();
        SimpleMetricMessageSender sender = new SimpleMetricMessageSender(metricMessage);
        //sender.insertEntryPoint();
        sender.insertExitPoint();
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
