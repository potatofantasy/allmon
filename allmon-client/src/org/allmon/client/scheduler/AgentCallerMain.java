package org.allmon.client.scheduler;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

    private String className;
    private int shift = 0;
    private String [] classParamsString;
    
    public static void main(String[] args) {
        logger.debug(AllmonLoggerConstants.ENTERED);
        boolean success = false;
        try {
	        if (args.length > 0) {
	            // run agent caller in the same thread
                AgentCallerMain agentCaller = new AgentCallerMain();
                
                agentCaller.className = args[0]; //mandatory parameter, ex: "org.allmon.client.agent.MetricCollector"
	            agentCaller.shift = Integer.parseInt(args[1]); //mandatory parameter, integer value (0 - 3600) shift time in sec
                agentCaller.setParameters(args);
                //logger.debug("loading class: " + className + ", for parameters: " + classParamsString);
                agentCaller.createInstanceAndExecute();
	            
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
    
    private void setParameters(String[] args) {
        logger.debug("param size: " + args.length);
        for (int i = 0; i < args.length; i++) {
            logger.debug("param[" + i + "]: " + args[i]);
        }
        
        classParamsString = null;
        if (args.length > 2) {
            //classParamsString = Arrays.copyOfRange(args, 1, args.length); // jdk-1.6
            classParamsString = new String[args.length - 2];
            for (int i = 0; i < classParamsString.length; i++) {
                classParamsString[i] = args[i + 2];
            }
        }
    }
    
    private void createInstanceAndExecute() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalClassFormatException {
        logger.debug("Preparing for scheduled execution of " + className + ".execute() waiting " + shift + "secs...");
        // waiting additional shift
        try {
            Thread.sleep(shift * 1000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        
        Class c = Class.forName(className);
        AgentContext agentContext = new AgentContext();
        try {
            //Object o = c.newInstance();
            Constructor constructor = c.getConstructor(AgentContext.class);
            Object agent = constructor.newInstance(agentContext);
            if (agent instanceof AgentTaskable) {
                AgentTaskable agentTask = (AgentTaskable)agent;
                executeAgentTaskable(agentTask);
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
    
    private void executeAgentTaskable(AgentTaskable task) {
        String taskClassName = task.getClass().getCanonicalName();
        task.setAgentSchedulerName(AgentSchedulerMain.NAME);
        if (classParamsString != null) {
            logger.debug("Set parameters for: " + taskClassName);
            task.setParameters(classParamsString);
        }
        logger.debug("Execution : " + taskClassName + ".execute() ...");
        task.execute();
        logger.debug("Execution : " + taskClassName + ".execute() finished");
    }
        
}
