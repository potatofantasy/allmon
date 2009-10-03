package org.allmon.server.loader;

import it.sauronsoftware.cron4j.Scheduler;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoadAllmetricMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    private static final Log logger = LogFactory.getLog(LoadAllmetricMain.class);

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
        // Schedule extracing raw metrics data and loading them to allmon allmetrics schema
        scheduler.schedule("* * * * *", new Runnable() {
            public void run() {
                LoadRawMetric l = new LoadRawMetric();
                l.loadAllmetric();
            }
        });
        
        // Starts the scheduler
        scheduler.start();
        // Will run for five minutes
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
        }
        // Stops the scheduler.
        scheduler.stop();
        // Scheduler has killed all open and running tasks
    
    }
    
}
