package org.allmon.client.controller.neuralrules;

import javax.jms.ConnectionFactory;

import org.allmon.common.AllmonActiveMQConnectionFactory;
import org.allmon.common.AllmonCommonConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NeuralRulesReceiver {
	
	private static final Log logger = LogFactory.getLog(NeuralRulesReceiver.class);
	
	public NeuralRulesReceiver() {
		logger.debug("Camel context is being initialized");
		System.out.println("Camel context is being initialized");
		CamelContext context = new DefaultCamelContext();
		// Set up the ActiveMQ JMS Components
		ConnectionFactory connectionFactory = AllmonActiveMQConnectionFactory.server();
		context.addComponent(AllmonCommonConstants.ALLMON_CAMEL_JMSQUEUE,
				JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		try {
			context.addRoutes(new NeuralRulesReceiverRouteBuilder());
			context.start();
			logger.debug("Camel context has been started");
		} catch (Exception e) {
			logger.debug(e, e);
		}
	}

}