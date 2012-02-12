package org.allmon.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.allmon.common.MetricMessage;

public class MetricsDataStore {

//	private HashMap<MetricsKey, MetricMessage> metricsMap = new HashMap<MetricsKey, MetricMessage>();
//	private HashMap<String, MetricMessage> metricsMap = new HashMap<String, MetricMessage>();
//	private HashMap<String, Double> metricsMap = new HashMap<String, Double>();
	private HashMap<String, List<MetricMessage>> metricsMap = new HashMap<String, List<MetricMessage>>();
	private int valuesCount = 0;
	
	private int activityTime = 5 * 60000; // metrics are kept only for last 5 minutes
	
	public MetricsDataStore() {
		Timer timer = new Timer("MetricsDataStore-CleanerThread", true);
		TimerTask task = new TimerTask() {
			public void run() {
				removeOldMetrics(activityTime);
			}
		};
		timer.schedule(task, 100, 1000);
		//timer.cancel();
		
//		Runnable r = new Runnable() {
//			public void run() {
//				while(true) {
//					System.out.println("run");
//				}
//			}
//		};
//		Thread cleaner = new Thread(r, "Cleaner");
//		cleaner.start();
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
			return metricsListAll.get(metricsListAll.size() - 1);
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
		// TODO potentially add metrics sorted in time 
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
		Set<String> keySet = metricsMap.keySet();
		for (String key : keySet) {
			List<MetricMessage> metrics = metricsMap.get(key);
			for (int i = 0; i < metrics.size(); i++) {
				MetricMessage metricMessage = metrics.get(i);
				if (metricMessage.getEventTime() < System.currentTimeMillis() - activityTime) {
					metrics.remove(i);
					valuesCount--;
				}
			}
		}
	}
	
}