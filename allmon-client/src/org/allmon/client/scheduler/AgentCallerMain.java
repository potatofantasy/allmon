package org.allmon.client.scheduler;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Arrays;

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
	            String [] classParamsString = null;
	            if (args.length > 1) {
	                classParamsString = Arrays.copyOfRange(args, 1, args.length);
	            }
	            logger.debug("Loading class : " + className);
	            
	            // run in the same thread
	            AgentCallerMain agentCaller = new AgentCallerMain();
	            agentCaller.createInstanceAndExecute(className, classParamsString);
                
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
    
    void createInstanceAndExecute(String className, String [] classParamsString) 
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalClassFormatException {
        Class c = Class.forName(className);
        Object o = c.newInstance();
        if (o instanceof AgentTaskable) {
            AgentTaskable task = (AgentTaskable)o;
            executeAgentTaskable(task, classParamsString);
        } else {
            throw new IllegalClassFormatException("The class (" + c.getCanonicalName() + ") passed as a parameter has to extend " + AgentTaskable.class.getName());
        }
    }
    
    void executeAgentTaskable(AgentTaskable task, String [] classParamsString) {
        String taskClassName = task.getClass().getCanonicalName();
        if (classParamsString != null) {
            logger.debug("Set parameters for: " + taskClassName);
            task.setParameters(classParamsString);
        }
        logger.debug("Execution : " + taskClassName + ".execute() ...");
        task.execute(); //XXX is not finishing!
        logger.debug("Execution : " + taskClassName + ".execute() finished");
    }
    
}
