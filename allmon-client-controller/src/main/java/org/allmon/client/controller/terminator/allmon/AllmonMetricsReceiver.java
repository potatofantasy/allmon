package org.allmon.client.controller.terminator.allmon;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AllmonMetricsReceiver {

	private static final Log logger = LogFactory.getLog(AllmonMetricsReceiver.class);

	public void receiveData() throws Exception {
		CamelContext context = new DefaultCamelContext();
		// Set up the ActiveMQ JMS Components
		ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.server();
		context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE,
				JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		context.addRoutes(new MetricsReceiverRouteBuilder());
		context.start();
		logger.debug("Camel context has been started");
	}

}
