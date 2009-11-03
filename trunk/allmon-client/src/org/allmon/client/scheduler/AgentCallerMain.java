package org.allmon.client.scheduler;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.allmon.client.agent.AgentContext;
import org.allmon.client.agent.AgentTaskable;
import org.allmon.common.AllmonLoggerConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is run by AgentSchedulerMain to call execute method of ActiveAgents 
 * (implementing interface AgentTaskable). This class also sets parameters ActiveAgent
 * instances.<br><br>
 * 
 * NOTE:
 * This class is called by cron4j (run by AgentSchedulerMain) and is deployed 
 * under allmon-client.jar. If you added any changes to Agents functionality the jar
 * file has to be rebuilt.
 * 
 */
public class AgentCallerMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    private static final Log logger = LogFactory.getLog(AgentCallerMain.class);
    
    public static void main(String[] args) {
        logger.debug(AllmonLoggerConstants.ENTERED);
        boolean success = false;
        try {
	        if (args.length > 0) {
	            // run agent caller in the same thread
                AgentCallerMain agentCaller = new AgentCallerMain();
                
	            String className = args[0]; //ex: "org.allmon.client.agent.MetricCollector";
	            String [] classParamsString = agentCaller.decodeParameters(args);
                //logger.debug("loading class: " + className + ", for parameters: " + classParamsString);
                agentCaller.createInstanceAndExecute(className, classParamsString);
	            
                success = true;
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
        	if (success) {
        		logger.debug("run finished successfully");
        	} else {
        		logger.debug("run finished NOT successfully");
        	}
        }
        logger.debug(AllmonLoggerConstants.EXITED);
    }
    
    private String[] decodeParameters(String[] args) {
        logger.debug("param size: " + args.length);
        for (int i = 0; i < args.length; i++) {
            logger.debug("param[" + i + "]: " + args[i]);
        }
        
        String [] classParamsString = null;
        if (args.length > 1) {
            classParamsString = Arrays.copyOfRange(args, 1, args.length);
        }
        return classParamsString;
    }
    
    private void createInstanceAndExecute(String className, String [] classParamsString) 
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalClassFormatException {
        Class c = Class.forName(className);
        AgentContext agentContext = new AgentContext();
        try {
            //Object o = c.newInstance();
            Constructor constructor = c.getConstructor(AgentContext.class);
            Object agent = constructor.newInstance(agentContext);
            if (agent instanceof AgentTaskable) {
                AgentTaskable agentTask = (AgentTaskable)agent;
                executeAgentTaskable(agentTask, classParamsString);
            } else {
                throw new IllegalClassFormatException("The class (" + c.getCanonicalName() + ") passed as a parameter has to extend " + AgentTaskable.class.getName());
            }
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        } finally {
            // closing agent context
            agentContext.stop();
        }
    }
    
    private void executeAgentTaskable(AgentTaskable task, String [] classParamsString) {
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
