package org.allmon.server.receiver;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;
import org.allmon.server.loader.RawMetric2;
import org.allmon.server.loader.RawMetricFactory;

public class MetricMessageConverter {
	
	public RawMetric2[] convert(MetricMessageWrapper metricMessageWrapper) {
		int metricMessageWrapperSize = metricMessageWrapper.size();
		RawMetric2[] rawMetrics = new RawMetric2[metricMessageWrapperSize];
		for (int i = 0; i < metricMessageWrapperSize; i++) {
			MetricMessage metricMessage = metricMessageWrapper.get(i);
			rawMetrics[i] = convert(metricMessage);
		}
		return rawMetrics;
	}
	
	public RawMetric2 convert(MetricMessage metricMessage) {
		RawMetric2 rawMetric = RawMetricFactory.createApplicationActionServletRawMetric(
				metricMessage.getHost(),
				metricMessage.getHostIp(),
				metricMessage.getInstance(),
				metricMessage.getResource(),
				metricMessage.getSource(),
				metricMessage.getDurationTime(),
				metricMessage.getEventTime(),
				metricMessage.getParametersString(),
				metricMessage.getExceptionString());
		return rawMetric;
	}
	
}
