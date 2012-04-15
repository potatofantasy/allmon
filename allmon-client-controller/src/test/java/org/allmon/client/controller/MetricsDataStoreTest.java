package org.allmon.client.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.allmon.client.controller.MetricsDataStore;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MetricsDataStoreTest {

	private MetricsDataStore dataStore = new MetricsDataStore();
	private MetricMessage metric1, metric2, metric3, metric4, metric5;

	@Before
	public void prepareDate() throws InterruptedException {
		// "acquire" metrics
		metric1 = MetricMessageFactory.createOsMessage("CPU", "CPU1", 0.1, null);
		Thread.sleep(50);
		metric2 = MetricMessageFactory.createOsMessage("CPU", "CPU1", 0.2, null);
		Thread.sleep(50);
		metric3 = MetricMessageFactory.createOsMessage("CPU", "CPU1", 0.15, null);
		Thread.sleep(50);
		metric4 = MetricMessageFactory.createOsMessage("CPU", "CPU1", 0.17, null);
		Thread.sleep(100);
		metric5 = MetricMessageFactory.createOsMessage("CPU", "CPU2", 0.4, null);

		dataStore.put(metric1); // quite random adding....
		dataStore.put(metric5);
		dataStore.put(metric4);
		dataStore.put(metric3);
		dataStore.put(metric2);

		assertEquals(5, dataStore.valuesCount());
		assertEquals(2, dataStore.keysCount());
	}
	
	@Test
	public void searchAllMetrics() {
		List<MetricMessage> metrics = dataStore.get(MetricsDataStore.resourceKey(metric1));
		
		assertEquals(4, metrics.size());
		assertEquals(0.17, metrics.get(0).getMetricValue(), 0);
		assertEquals(0.15, metrics.get(1).getMetricValue(), 0);
		assertEquals(0.2, metrics.get(2).getMetricValue(), 0);
		assertEquals(0.1, metrics.get(3).getMetricValue(), 0);
	}
	
	@Test
	public void searchLatestsMetrics() {
		List<MetricMessage> metrics = dataStore.get(MetricsDataStore.resourceKey(metric1), 160);

		assertEquals(2, metrics.size());
		assertEquals(0.17, metrics.get(0).getMetricValue(), 0);
		assertEquals(0.15, metrics.get(1).getMetricValue(), 0);
	}

	@Test
	public void searchLatestsMetric() {
		MetricMessage metric = dataStore.getLatest(MetricsDataStore.resourceKey(metric1));

		assertEquals(0.17, metric.getMetricValue(), 0);
	}

	@Test
	public void removeOldMetric() {
		List<MetricMessage> metrics = dataStore.get(MetricsDataStore.resourceKey(metric1));
		
		assertEquals(4, metrics.size());
		assertEquals(5, dataStore.valuesCount());
		assertEquals(2, dataStore.keysCount());
		assertEquals(0.17, metrics.get(0).getMetricValue(), 0);
		assertEquals(0.15, metrics.get(1).getMetricValue(), 0);
		assertEquals(0.2, metrics.get(2).getMetricValue(), 0);
		assertEquals(0.1, metrics.get(3).getMetricValue(), 0);
		
		dataStore.removeOldMetrics(210);
		List<MetricMessage> metrics2 = dataStore.get(MetricsDataStore.resourceKey(metric1));
		assertEquals(3, metrics2.size());
		assertEquals(4, dataStore.valuesCount());
		assertEquals(2, dataStore.keysCount());
		assertEquals(0.17, metrics2.get(0).getMetricValue(), 0);
		assertEquals(0.15, metrics2.get(1).getMetricValue(), 0);
		assertEquals(0.2, metrics2.get(2).getMetricValue(), 0);
		
		dataStore.removeOldMetrics(0); // remove all
		List<MetricMessage> metrics3 = dataStore.get(MetricsDataStore.resourceKey(metric1));
		assertNull(metrics3);
		assertEquals(0, dataStore.valuesCount());
		assertEquals(0, dataStore.keysCount());
		
	}
	
	@Test
	@Ignore
	public void simpleLoadTest() throws InterruptedException {
		// acquiring metrics
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			dataStore.put(MetricMessageFactory.createOsMessage("CPU", "CPU2", 0.4, null));
		}
		long t1 = System.currentTimeMillis();
		
		// searching metrics 'get'
		List<MetricMessage> metrics = null;
		for (int i = 0; i < 100000; i++) {
			metrics = dataStore.get(MetricsDataStore.resourceKey(metric1));
		}
		long t2 = System.currentTimeMillis();
		
		// searching metrics 'getLatest'
		List<MetricMessage> metrics2 = null;
		for (int i = 0; i < 100000; i++) {
			metrics2 = dataStore.get(MetricsDataStore.resourceKey(metric1), 300);
		}
		long t3 = System.currentTimeMillis();
		
		System.out.println(" acquiring 100'000 metrics:" + (t1 - t0));
		System.out.println(" searching metrics 'get' (100'000x):" + (t2 - t1));
		System.out.println(" searching metrics 'getLatest' (100'000x):" + (t3 - t2));
		
	}

}
