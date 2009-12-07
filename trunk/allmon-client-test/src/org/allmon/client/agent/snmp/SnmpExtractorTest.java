package org.allmon.client.agent.snmp;

import static org.junit.Assert.*;

import org.junit.Test;

public class SnmpExtractorTest {

	@Test
	public void testExtractData() {
		SnmpExtractor extractor = new SnmpExtractor();
		SnmpResponse response = extractor.extractData("1.3.6.1.2.1.25.3.3.1.2.768 = 176");
		assertNotNull(response);
		assertEquals("value of the response is wrong", response.getValue(), "176");
		assertEquals("oid of the response is wrong", response.getOid(), "1.3.6.1.2.1.25.3.3.1.2.768");
	}

}
