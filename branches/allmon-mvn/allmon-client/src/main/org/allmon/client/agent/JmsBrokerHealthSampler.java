package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class runs internally separate Scheduler thread which 
 * checks every minute if JMS broker is up and listening.<br><br>
 * 
 * This class is a singleton. It means that only one instance of this class
 * can be initialised in JVM and since this moment the class will constantly 
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
	private static final String matchString = "TCP.*:61616"; // "61616.*LISTENING";
	
	private static final long heartBeatRate = AllmonCommonConstants.ALLMON_CLIENT_BROKER_HEALTH_SAMPLER_HEARTBEATRATE;
    
	private Thread checkerProcess;
	private boolean poisonPill = false;
	
	private boolean brokerUp = false;
	private long lastCheckTime;
    
	/**
	 * Creating a new thread which runs Scheduler which checks 
	 * every minute if JMS broker is up and listening.
	 */
	private void runCheckerProcess() {
	    checkerProcess = new Thread(new Runnable() {
	    	public void run() {
//                // Creates a Scheduler instance
//                Scheduler scheduler = new Scheduler();
//                
//                // schedule and run the JMS broker checking job
//                scheduler.schedule("* * * * *", new Runnable() {
//                    public void run() {
//                        JmsBrokerHealthSampler.getInstance().checkJmsBrokerIsUp();
//                    }
//                });
//                
//                // Starts the scheduler
//                scheduler.start();
//                // Will run for five minutes
//                try {
//                    Thread.sleep(Long.MAX_VALUE);
//                } catch (InterruptedException e) {
//                }
//                // Stops the scheduler.
//                scheduler.stop();
//                // Scheduler has killed all open and running tasks
                
            	logger.info("run and keep checking ...");
                try {
                    while (!poisonPill) {
                        try {
                            Thread.sleep(heartBeatRate);
                        } catch (InterruptedException e) {
                        }
                        if (!poisonPill) {
                        	JmsBrokerHealthSampler.getInstance().checkJmsBrokerIsUp();
                        }
                    }
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);  
                } finally {
                    logger.warn("run method has been finished - check won't be performed anymore");  
                }
                
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
    
    /**
     * Checks in the same thread if JMS broker instance is up 
     * and store this status in brokerUp flag.
     * 
     * @return
     */
	public synchronized boolean checkJmsBrokerIsUp() {
		try {
			logger.debug("Executing shell command: [" + shellCommand + "] to check if JMS broker instanceis up...");
			Process p = Runtime.getRuntime().exec(shellCommand);
	    	//p.waitFor();
            logger.debug("Shell command has been executed successfully.");
	        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String match = OutputParser.findFirst(br, matchString);
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
		return brokerUp;
	}

	/**
	 * Retrieves brokerUp flag.
	 * 
	 * @return
	 */
	public boolean isJmsBrokerUp() {
		return brokerUp;
	}
	
	public long getLastCheckTime() {
		return lastCheckTime;
	}
	
	public void terminateProcess() {
		checkerProcess.interrupt(); // immediately interrupting checking thread
		poisonPill = true; // avoids checking last time
	}
	
}
