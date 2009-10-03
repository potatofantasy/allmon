package org.allmon.client.scheduler;

import org.allmon.client.agent.AllmonAgentTask;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentCallerMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    private static final Log logger = LogFactory.getLog(AgentCallerMain.class);
    
    public static void main(String[] args) {
        logger.debug(AllmonLoggerConstants.ENTERED);
        logger.debug("param size : " + args.length);
        
        if (args.length > 0) {
            String className = args[0]; //ex: "org.allmon.client.agent.MetricCollector";
            logger.debug("Loading class : " + className);
            
            // run in the same thread
            try {
                Class c = Class.forName(className);
                Object o = c.newInstance();
                if (o instanceof AllmonAgentTask) {
                    AllmonAgentTask task = (AllmonAgentTask)o;
                    logger.debug("Execution : " + className + ".execute() ...");
                    task.execute();
                    logger.info("Execution : " + className + ".execute() finished");
                } else {
                    throw new Exception("The class (" + className + ") passed as a parameter has to extend " + AllmonAgentTask.class.getName());
                }
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            } catch (InstantiationException e) {
                logger.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
            
        }
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
