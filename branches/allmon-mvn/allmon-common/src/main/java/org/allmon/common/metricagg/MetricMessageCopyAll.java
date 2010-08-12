package org.allmon.common.metricagg;

import org.allmon.common.MetricMessageCumulatorMethod;
import org.allmon.common.MetricMessageWrapper;

public class MetricMessageCopyAll extends MetricMessageCumulatorMethod {

	public MetricMessageWrapper cumulate(MetricMessageWrapper metricMessageWrapper) {
		return metricMessageWrapper;
	}
}
