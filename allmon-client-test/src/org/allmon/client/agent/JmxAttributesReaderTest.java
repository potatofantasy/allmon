package org.allmon.client.agent;

import java.util.List;

import junit.framework.TestCase;

import org.allmon.client.agent.jmx.JmxAttributesReader;
import org.allmon.client.agent.jmx.MBeanAttributeData;
import org.allmon.common.AllmonPropertiesReader;

import sun.tools.jconsole.LocalVirtualMachine;

public class JmxAttributesReaderTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    public void testLVMList() throws Exception {
        JmxAttributesReader jmxReader = new JmxAttributesReader();

        List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName(), false);
        assertEquals(1, lvmList.size());

        LocalVirtualMachine lvm = lvmList.get(0);
        assertNotSame(0, lvm.vmid());
    }
	
	public void testMBeansAttributes() throws Exception {
		JmxAttributesReader jmxReader = new JmxAttributesReader();

        List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName(), false);
        assertTrue(lvmList.size() > 0);

        LocalVirtualMachine lvm = lvmList.get(0);
        assertNotSame(0, lvm.vmid());

        List<MBeanAttributeData> attributeDataList = jmxReader.getMBeansAttributesData(lvm, "", false);
        assertTrue(attributeDataList.size() > 0);

		List<MBeanAttributeData> attributeDataListMemoryUsed = 
            jmxReader.getMBeansAttributesData(lvm, ".*java.lang:type=Memory:.*HeapMemoryUsage/used", false);
        assertEquals(2, attributeDataListMemoryUsed.size()); 
        // 1) sun.management.MemoryImpl:java.lang:type=Memory:HeapMemoryUsage/used
        // 2) sun.management.MemoryImpl:java.lang:type=Memory:NonHeapMemoryUsage/used
        
        List<MBeanAttributeData> attributeDataListGc = 
            jmxReader.getMBeansAttributesData(lvm, "sun.management.GarbageCollector", false);
        assertTrue(attributeDataListGc.size() > 0);

        List<MBeanAttributeData> attributeDataListMemory = 
            jmxReader.getMBeansAttributesData(lvm, "sun.management.Memory", false); // MemoryPool
        assertTrue(attributeDataListMemory.size() > 0);

        List<MBeanAttributeData> attributeDataListOs = 
            jmxReader.getMBeansAttributesData(lvm, "sun.management.OperatingSystem", false);
        assertTrue(attributeDataListOs.size() > 0);

    }
    
    public void testMBeansAttributesLVMListAllOs() throws Exception {
        JmxAttributesReader jmxReader = new JmxAttributesReader();

        List<LocalVirtualMachine> lvmList = jmxReader.getLocalVirtualMachine("", false);
        assertTrue(lvmList.size() > 0);
        
        for (LocalVirtualMachine lvm : lvmList) {
            assertTrue(lvm.vmid() > 0);
            try {
                List<MBeanAttributeData> attributeDataListOs = 
                    jmxReader.getMBeansAttributesData(lvm, "sun.management.OperatingSystem", false);
                assertTrue(attributeDataListOs.size() > 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
