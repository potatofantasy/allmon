package org.allmon.common.metricagg;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageCumulatorMethod;
import org.allmon.common.MetricMessageWrapper;

public class MetricMessageSimpleSummator extends MetricMessageCumulatorMethod {

	public MetricMessageWrapper cumulate(MetricMessageWrapper metricMessageWrapper) {
		MetricMessageWrapper returnWrapper = new MetricMessageWrapper();
		
		double summary = 0;
		for (MetricMessage metricMessage : metricMessageWrapper) {
			summary += metricMessage.getMetricValue();
		}
		
		MetricMessage metricMessage = createMetricMessage();
		returnWrapper.add(metricMessage);
		
		return returnWrapper;
	}
}
