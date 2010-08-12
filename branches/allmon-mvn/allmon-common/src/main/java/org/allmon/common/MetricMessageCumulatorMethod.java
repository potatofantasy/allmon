package org.allmon.common;

public abstract class MetricMessageCumulatorMethod {

	public abstract MetricMessageWrapper cumulate(MetricMessageWrapper metricMessageWrapper);

	protected MetricMessage createMetricMessage() {
		return new MetricMessage();
	}

}
