package org.allmon.client.scheduler;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AllmonActiveAgentClientMain {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AllmonActiveAgentClientMain.class);
    
	public static final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			new String[] { "classpath:" + AllmonCommonConstants.ALLMON_CLIENT_AGENT_ACTIVEAGENT_APPCONTEXT_CONF_FILENAME });
	
	public static void main(String[] args) {
		
		logger.info("Allmon active agents are starting work...");
		// ...

	}

}
