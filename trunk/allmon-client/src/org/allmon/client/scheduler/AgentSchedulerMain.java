package org.allmon.client.scheduler;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.File;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class runs simple scheduler to run all set up (configured) tasks
 * which will be responsible for gathering metrics *actively* from monitored
 * systems/environments. <br><br>
 * 
 * To meet active monitoring requirement we need to add a simple scheduling
 * mechanism. At this stage Apache Camel does not contain built-in simple
 * scheduler. Maybe situation changes in the future due to development related
 * to http://issues.apache.org/activemq/browse/CAMEL-1954. So, we are
 * introducing cron4j instead.
 * 
 */
public class AgentSchedulerMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    private static final Log logger = LogFactory.getLog(AgentSchedulerMain.class);

    public static void main(String[] args) {
        // Creates a Scheduler instance
        Scheduler scheduler = new Scheduler();
        
        // Schedule a once-a-minute task
        scheduler.schedule("* * * * *", new Runnable() {
            public void run() {
                if (logger.isDebugEnabled()) {
                    logger.debug("Another minute ticked away...");
                }
            }
        });

        // Schedule another task
        //ProcessTask task = new ProcessTask("C:\\Windows\\System32\\notepad.exe");
        //scheduler.schedule("* * * * *", task);
        
        // Schedule file - the file is re-read every minute
        scheduler.scheduleFile(new File("allmon-client.cron"));
        
        // Starts the scheduler
        scheduler.start();
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
        }
        // Stops the scheduler.
        scheduler.stop();
        // Scheduler has killed all open and running tasks
    }

}