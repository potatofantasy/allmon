package org.allmon.client.agent;

import java.util.List;

import junit.framework.TestCase;

import org.allmon.client.agent.JmxAttributesReader.MBeanAttributeData;

import sun.tools.jconsole.LocalVirtualMachine;

public class JmxAttributesReaderTest extends TestCase {

	public void testLVMList() throws Exception {
		JmxAttributesReader jmxReader = new JmxAttributesReader();

		List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName());
		assertTrue(lvmList.size() > 0);
		
		LocalVirtualMachine lvm = lvmList.get(0);
		assertTrue(lvm.vmid() > 0);
	}
	
	public void testMBeansAttributes() throws Exception {
		JmxAttributesReader jmxReader = new JmxAttributesReader();

		List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName());
		assertTrue(lvmList.size() > 0);

		LocalVirtualMachine lvm = lvmList.get(0);
		assertTrue(lvm.vmid() > 0);
		
		List<MBeanAttributeData> attributeDataList = jmxReader.getMBeansAttributesData(lvm, "");
		assertTrue(attributeDataList.size() > 0);
		
		List<MBeanAttributeData> attributeDataListGc = jmxReader.getMBeansAttributesData(lvm, "GarbageCollector");
		assertTrue(attributeDataListGc.size() > 0);
		
		
	}

}
