package org.allmon.client.controller.terminator;

import org.allmon.client.controller.terminator.allmon.AllmonMetricsReceiver;


public abstract class AbstractAllmonMetricsJavaCallTerminatorController extends AbstractJavaCallTerminatorController {

	// TODO get data from Aggregator (for controller use queue), raw metrics and SLA calculated values
	// TODO point to controlled application scope metrics retriever
	
	private AllmonMetricsReceiver allmonMetricsReceiver;
	
	
	
}
