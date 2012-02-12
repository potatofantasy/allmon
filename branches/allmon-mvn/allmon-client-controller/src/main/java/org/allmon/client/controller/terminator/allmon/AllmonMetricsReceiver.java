package org.allmon.client.controller.terminator.allmon;

import javax.jms.ConnectionFactory;

import org.allmon.client.controller.MetricsDataStore;
import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AllmonMetricsReceiver {

	private static final Log logger = LogFactory.getLog(AllmonMetricsReceiver.class);

//	public static final HashMap<String, String> metricsDataStore = new HashMap<String, String>();
	public static final MetricsDataStore metricsDataStore = new MetricsDataStore();
	
	public AllmonMetricsReceiver() {
		logger.debug("Camel context is being initialized");
		System.out.println("Camel context is being initialized");
		CamelContext context = new DefaultCamelContext();
		// Set up the ActiveMQ JMS Components
		ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.server();
		context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE,
				JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		try {
			context.addRoutes(new MetricsReceiverRouteBuilder());
			context.start();
			logger.debug("Camel context has been started");
		} catch (Exception e) {
			logger.debug(e, e);
		}
	}

}
