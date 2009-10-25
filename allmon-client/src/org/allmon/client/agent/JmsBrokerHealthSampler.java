package org.allmon.client.agent;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class runs internally separate Scheduler thread which 
 * checks every minute if JMS broker is up and listening.<br><br>
 * 
 * This class is a singleton. It means that only one instance of this class
 * can be initialized in JVM and since this moment the class will constantly 
 * monitor JMS broker instance.<br><br>
 * 
 * <b>Every JVM instance which uses <u>an agent</u> has this class instantiated 
 * for the whole live time.</b>
 * 
 */
public class JmsBrokerHealthSampler {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
	
	private static final Log logger = LogFactory.getLog(JmsBrokerHealthSampler.class);
    
	private static final String shellCommand = "netstat -an";
	//private static final String shellCommand = "netstat -an|find \"61616\""; // problems with running the command
	
	private boolean brokerUp = false;
	private long lastCheckTime;
    
	/**
	 * Creating a new thread which runs Scheduler which checks 
	 * every minute if JMS broker is up and listening.
	 */
	private void runCheckerProcess() {
	    Thread checkerProcess = new Thread(new Runnable() {
            public void run() {
                // Creates a Scheduler instance
                Scheduler scheduler = new Scheduler();
                
                // schedule and run the JMS broker checking job
                scheduler.schedule("* * * * *", new Runnable() {
                    public void run() {
                        JmsBrokerHealthSampler.getInstance().checkJmsBrokerIsUp();
                    }
                });
                
                // Starts the scheduler
                scheduler.start();
                // Will run for five minutes
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                }
                // Stops the scheduler.
                scheduler.stop();
                // Scheduler has killed all open and running tasks
            }
	    });
	    checkerProcess.start();
	}
	
	/**
	 * Private constructor prevents instantiation from other classes.
	 * 
	 * Constructor: 
	 * (1) checks immediately if JMS broker is up and listening and 
	 * (2) runs checker process to perform the same check every minute.
	 */
	private JmsBrokerHealthSampler() {
	    runCheckerProcess();
	    checkJmsBrokerIsUp();
	}
    
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final JmsBrokerHealthSampler instance = new JmsBrokerHealthSampler();
    }
    
    public static JmsBrokerHealthSampler getInstance() {
        return SingletonHolder.instance;
    }
    
	public synchronized void checkJmsBrokerIsUp() {
		try {
			logger.debug("Executing shell command: [" + shellCommand + "] to check if JMS broker instanceis up...");
			Process p = Runtime.getRuntime().exec(shellCommand);
	    	//p.waitFor();
            logger.debug("Shell command has been executed successfully.");
	        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String match = OutputParser.findFirst(br, "61616.*LISTENING");
	        p.destroy();
	        if (!"".equals(match)) {
	        	brokerUp = true;
	        } else {
	        	brokerUp = false;
	        }
	        lastCheckTime = System.currentTimeMillis();
		} catch (IOException e) {
			brokerUp = false;
			logger.error(e.getMessage(), e);
		}
		
	}

	public boolean isJmsBrokerUp() {
		return brokerUp;
	}
	
	public long getLastCheckTime() {
		return lastCheckTime;
	}
	
}
