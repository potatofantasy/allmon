package org.allmon.common;

import java.util.ArrayList;
import java.util.List;

import org.allmon.common.metricagg.MetricMessageCopyAll;

/**
 * This class is used to execute simple cumulator algorithm on collected raw metrics data 
 * just before sending data from client. It can be used on all those metrics which are collected 
 * in huge amounts and which level of detail can be aggregated.
 * 
 * It is a similar mechanism to aggregator process on allmon metrics storage side.
 * 
 * TODO this mechanism needs more development 
 */
public class MetricMessageCumulator {
	
	private MetricMessageCumulatorMethod cumulatorMethod;
	
	public MetricMessageCumulator(MetricMessageCumulatorMethod cumulatorMethod) {
		this.cumulatorMethod = cumulatorMethod;
	}
	
	public MetricMessageWrapper execute(MetricMessageWrapper metricMessageWrapper) {
		return cumulatorMethod.cumulate(metricMessageWrapper);
	}
	
	/**
	 * Extract all cumulator methods needed for specified metric wrapper
	 * 
	 * @param metricMessageWrapper
	 * @return
	 */
	public static List<MetricMessageCumulatorMethod> getAllMethods(MetricMessageWrapper metricMessageWrapper) {
		ArrayList<MetricMessageCumulatorMethod> methods = new ArrayList<MetricMessageCumulatorMethod>();
		methods.add(new MetricMessageCopyAll());
		// TODO finish this functionality - iterate through the wrapper and check what needed
		return methods;
	}
	
}
