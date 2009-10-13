package org.allmon.client.scheduler;

import java.lang.instrument.IllegalClassFormatException;

import org.allmon.client.agent.AgentTaskable;
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
   
        boolean success = false;
        
        try {  
	        if (args.length > 0) {
	            String className = args[0]; //ex: "org.allmon.client.agent.MetricCollector";
	            logger.debug("Loading class : " + className);
	            
	            // run in the same thread
                Class c = Class.forName(className);
                Object o = c.newInstance();
                if (o instanceof AgentTaskable) {
                    AgentTaskable task = (AgentTaskable)o;
                    logger.debug("Execution : " + className + ".execute() ...");
                    task.execute(); //XXX is not finishing!
                    logger.debug("Execution : " + className + ".execute() finished");
                } else {
                    throw new IllegalClassFormatException("The class (" + className + ") passed as a parameter has to extend " + AgentTaskable.class.getName());
                }
                
                success = true;
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
        	if (success) {
        		logger.debug("Run finished successfully");
        	} else {
        		logger.debug("Run finished NOT successfully");
        	}
        }
        
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
}
