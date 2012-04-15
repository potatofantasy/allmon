package org.allmon.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.allmon.client.controller.terminator.allmon.AllmonMetricsReceiver;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageWrapper;

public class MetricsDataStore {

	private HashMap<String, List<MetricMessage>> metricsMap = new HashMap<String, List<MetricMessage>>();
	private int valuesCount = 0;
	
	private static final int METRICS_ACTIVITY_TIME = 5 * 60000; // metrics are kept only for last 5 minutes
	private static final int METRICS_SWEEPING_SCHEDULE_PERIOD_TIME = 10000; // metrics are swept every 10 seconds
	
	public MetricsDataStore() {
		Timer timer = new Timer("MetricsDataStore-CleanerThread", true);
		TimerTask task = new TimerTask() {
			public void run() {
				removeOldMetrics(METRICS_ACTIVITY_TIME);
			}
		};
		timer.schedule(task, METRICS_SWEEPING_SCHEDULE_PERIOD_TIME, METRICS_SWEEPING_SCHEDULE_PERIOD_TIME);
		//timer.cancel();
	}
	
	public List<MetricMessage> get(String key) {
		return metricsMap.get(key);
	}
	public List<MetricMessage> get(String key, long lastMillis) {
		List<MetricMessage> metricsListAll = metricsMap.get(key);
		List<MetricMessage> metricsList = new ArrayList<MetricMessage>();
		long lowestTime = System.currentTimeMillis() - lastMillis;
		for (MetricMessage metricMessage : metricsListAll) {
			if (metricMessage.getEventTime() > lowestTime) {
				metricsList.add(metricMessage);
			}
//			else return; // TODO - is possible when time sorting is enforced !!
		}
		return metricsList;
	}
	public MetricMessage getLatest(String key) {
		List<MetricMessage> metricsListAll = metricsMap.get(key);
		if (metricsListAll != null) {
			//return metricsListAll.get(metricsListAll.size() - 1); // where list in ascending order 
			return metricsListAll.get(0); // where list in descending order 
		}
		return null;
	}
	public MetricMessage getLatest(String key, long lastMillis) {
		MetricMessage metric = getLatest(key);
		if (metric != null && metric.getEventTime() > System.currentTimeMillis() - lastMillis) {
			return metric;
		}
		return null;
	}

	private List<MetricMessage> put(String key, MetricMessage metric) {
		List<MetricMessage> metricsList = metricsMap.get(key);
		if (metricsList == null) {
			metricsList = new ArrayList<MetricMessage>();
		}
		//metricsList.add(metric); // adding at the end assuming all messages are added as they are created
		// add metrics sorted in time of data acquisition - so the list is always ordered descending in time
		// search first element which is lesser than current metric, and add the metric just before this element
		for (int i = 0; i <  metricsList.size(); i++) {
			if (metricsList.get(i).getEventTime() <= metric.getEventTime()) {
				metricsList.add(i, metric);
				valuesCount++;
				return metricsMap.put(key, metricsList);
			}
		}
		
		// the oldest element added at the end
		metricsList.add(metric); 
		valuesCount++;
		return metricsMap.put(key, metricsList);
	}
	public List<MetricMessage> put(MetricMessage metric) {
//		put(resourceKey(metric, false, false, false, false, false), metric); // TODO prototype ???
		return put(resourceKey(metric), metric); // put with full Metric Message key
	}
	
	// TODO XXX remove!!!!
	public static String resourceKey(MetricMessage metric) {
    	return resourceKey(
    			metric.getHost(), metric.getHostIp(), metric.getInstance(), 
    			metric.getMetricType(), metric.getResource(), metric.getSource());
	}
	@Deprecated
	public static String resourceKey(MetricMessage metric, 
			boolean host, boolean hostIp, boolean instance, 
			boolean metricType, boolean source) {
    	return resourceKey(
    			host?metric.getHost():".*", hostIp?metric.getHostIp():".*", instance?metric.getInstance():".*", 
    			metricType?metric.getMetricType():".*", metric.getResource(), source?metric.getSource():".*");
	}
	public static String resourceKey(String host, String hostIp, String instance, 
			String metricType, String resource, String source) {
    	StringBuffer buffer = new StringBuffer();
		buffer.append(host);
		buffer.append(",");
		buffer.append(hostIp);
		buffer.append(",");
		buffer.append(instance);
		buffer.append(",");
		buffer.append(metricType);
		buffer.append(",");
		buffer.append(resource);
		buffer.append("/");
		buffer.append(source);
		return buffer.toString();
	}
	
	public int valuesCount() {
		return valuesCount;
	}
	public int keysCount() {
		return metricsMap.size();
	}
	public Set<String> keysSet() {
		return metricsMap.keySet();
	}
	
	public List<String> getMatchingResourceKeys(String resourceRegex) {
		List<String> matchingKeys = new ArrayList<String>();
		String [] allKeys = metricsMap.keySet().toArray(new String[0]);
		for (String key : allKeys) {
			if (key.matches(resourceRegex)) {
				matchingKeys.add(key);
			}
		}
		return matchingKeys;
	}
	
	public void removeOldMetrics(int activityTime) {
		long t0 = System.currentTimeMillis();
		String[] keySet = metricsMap.keySet().toArray(new String[0]);
		for (String key : keySet) {
			List<MetricMessage> metrics = metricsMap.get(key);
			// delete all old metrics of the key
			for (int i = 0; i < metrics.size(); i++) {
				if (metrics.get(i).getEventTime() <= System.currentTimeMillis() - activityTime) {
					metrics.remove(i--);
					valuesCount--;
				}
			}
			//clean up the key if no data in under the key
			if (metrics.size() == 0) {
				metricsMap.remove(key);
			}
		}
		System.out.println("removeOldMetrics >> " + (System.currentTimeMillis() - t0) + 
				"ms, keysCount()/valuesCount(): " + keysCount() + "/" + valuesCount());
	}
	
}