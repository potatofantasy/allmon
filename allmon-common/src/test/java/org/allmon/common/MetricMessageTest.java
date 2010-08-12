package org.allmon.common;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class MetricMessageTest extends TestCase {

	public void testParametersString() throws Exception {
		MetricMessage message = new MetricMessage();
		message.setParameters("StringString");
		assertEquals("StringString", message.getParameters());
	}

	public void testParametersArrayListString() throws Exception {
		MetricMessage message = new MetricMessage();
		ArrayList<String> list = new ArrayList<String>();
		list.add("String1");
		list.add("String2");
		message.setParameters(list);
		//System.out.println(message.getParametersString());
		assertEquals("{\"list\": [\n  \"String1\",\n  \"String2\"\n]}", message.getParametersString());
		//System.out.println(message.getParameters().toString());
		assertEquals("[String1, String2]", message.getParameters().toString());
	}
	
	public void testParametersNotSerializable() throws Exception {
		MetricMessage message = new MetricMessage();
		Object o = new Object() {
			String param = "param";
			Object obj = new HashMap();
			ArrayList list = new ArrayList();
			
		};
		message.setParameters(o);
		System.out.println("size: " + message.getParametersString().length() + ", body:\n" +message.getParametersString());
		//this not serializable object has been transformed to string in json format
		assertTrue(message.getParameters() instanceof String);
		assertTrue(message.getParametersString().length() > 0);
	}

}
