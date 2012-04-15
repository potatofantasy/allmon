package org.allmon.client.controller.neuralrules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResourceTest {

	@Test
	public void testDenormalize() {
		assertEquals(0.2, new Resource("CPU", 0, 1).denormalize(0.2), 0);
		
		assertEquals(2, new Resource("XXX", 0, 10).denormalize(0.2), 0);
		
		assertEquals(2.777 + (1032.777 - 2.777) * 0.2, 
				new Resource("SLA2", 2.777, 1032.777).denormalize(0.2), 0);
	}

	@Test
	public void testNormalize() {
		assertEquals(0.2, new Resource("CPU", 0, 1).normalize(0.2), 0);
		
		assertEquals(0.2, new Resource("DiskQueue", 0, 100).normalize(20), 0);
		
		assertEquals(2.777 + (1032.777 - 2.777) * 0.2, 
				new Resource("SLA2", 2.777, 1032.777).denormalize(0.2), 0);
	}

	
}
