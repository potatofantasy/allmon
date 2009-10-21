package org.allmon.client.agent;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JmsBrokerChecker {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
	
	private static final Log logger = LogFactory.getLog(JmsBrokerChecker.class);
    
	private static final String shellCommand = "netstat ......";
	
	private static boolean brokerUp = false;
	private static long lastCheckTime;
	
	private JmsBrokerChecker() {
	}
	
	// TODO think where to run it
	public static void scheduleAndRunJob() {
		Scheduler scheduler = new Scheduler();
        scheduler.schedule("* * * * *", new Runnable() {
            public void run() {
                JmsBrokerChecker.checkJmsBrokerIsUp();
            }
        });
        scheduler.start();
	}
	
	public static synchronized void checkJmsBrokerIsUp() {
		try {
			logger.debug("Executing shell command: [" + shellCommand + "] to check if JMS broker instanceis up...");
			Process p = Runtime.getRuntime().exec(shellCommand);
	    	//p.waitFor();
			logger.debug("Shell command has been executed successfully.");
	        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
	        String match = OutputParser.findFirst(br, ".*");
	        //s.matches(".*")
	        if ("123".equals(match)) {
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

	public static boolean isJmsBrokerUp() {
		return brokerUp;
	}
	
	public static long getLastCheckTime() {
		return lastCheckTime;
	}
	
}
