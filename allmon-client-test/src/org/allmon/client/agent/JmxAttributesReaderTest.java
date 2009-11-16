package org.allmon.client.agent;

import java.util.List;

import junit.framework.TestCase;

import org.allmon.client.agent.JmxAttributesReader.MBeanAttributeData;
import org.allmon.common.AllmonPropertiesReader;

import sun.tools.jconsole.LocalVirtualMachine;

public class JmxAttributesReaderTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
	public void testLVMList() throws Exception {
		JmxAttributesReader jmxReader = new JmxAttributesReader();

		List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName());
		assertEquals(1, lvmList.size());
		
		LocalVirtualMachine lvm = lvmList.get(0);
		assertNotSame(0, lvm.vmid());
	}
	
	public void testMBeansAttributes() throws Exception {
		JmxAttributesReader jmxReader = new JmxAttributesReader();

		List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName());
		assertEquals(1, lvmList.size());
		
		LocalVirtualMachine lvm = lvmList.get(0);
		assertNotSame(0, lvm.vmid());
		
		List<MBeanAttributeData> attributeDataList = jmxReader.getMBeansAttributesData(lvm, "");
		assertTrue(attributeDataList.size() > 0);

		List<MBeanAttributeData> attributeDataListMemoryUsed = jmxReader.getMBeansAttributesData(lvm, "Memory.*HeapMemoryUsage:used");
		assertEquals(1, attributeDataListMemoryUsed.size());
		
		List<MBeanAttributeData> attributeDataListGc = jmxReader.getMBeansAttributesData(lvm, "sun.management.GarbageCollector");
		assertTrue(attributeDataListGc.size() > 0);
		
		List<MBeanAttributeData> attributeDataListMemory = jmxReader.getMBeansAttributesData(lvm, "sun.management.Memory"); //MemoryPool
		assertTrue(attributeDataListMemory.size() > 0);

		List<MBeanAttributeData> attributeDataListOs = jmxReader.getMBeansAttributesData(lvm, "sun.management.OperatingSystem");
		assertTrue(attributeDataListOs.size() > 0);
		
	}

}