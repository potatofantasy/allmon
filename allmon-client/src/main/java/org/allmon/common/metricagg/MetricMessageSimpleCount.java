package org.allmon.common.metricagg;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageCumulatorMethod;
import org.allmon.common.MetricMessageWrapper;

public class MetricMessageSimpleCount extends MetricMessageCumulatorMethod {

	public MetricMessageWrapper cumulate(MetricMessageWrapper metricMessageWrapper) {
		MetricMessageWrapper returnWrapper = new MetricMessageWrapper();
		
		MetricMessage metricMessage = createMetricMessage();
		metricMessage.setMetricValue(metricMessageWrapper.size());
		returnWrapper.add(metricMessage);
		
		return returnWrapper;
	}
}
