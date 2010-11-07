package org.allmon.client.agent.jmx;

import java.util.List;

import junit.framework.TestCase;

import org.allmon.client.agent.jmx.LocalVirtualMachineDescriptor;
import org.allmon.client.agent.jmx.JmxAttributesReader;
import org.allmon.client.agent.jmx.MBeanAttributeData;
import org.allmon.common.AllmonPropertiesReader;

public class JmxAttributesReaderTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }

    public void testLVMList() throws Exception {
        JmxAttributesReader jmxReader = new JmxAttributesReader(".*");

        List<LocalVirtualMachineDescriptor> lvmList = jmxReader.getLocalVirtualMachine(this.getClass().getName(), false);
        assertEquals(1, lvmList.size());

        LocalVirtualMachineDescriptor lvm = lvmList.get(0);
        assertNotSame(0, lvm.getVMid());
    }
	
	public void testMBeansAttributes() throws Exception {
		List<MBeanAttributeData> attributeDataList = 
			new JmxAttributesReader(".*java.lang:type=Memory.*HeapMemoryUsage/used").
					getAttributesFromLocal(this.getClass().getName());
        assertTrue(attributeDataList.size() > 0);
        
		List<MBeanAttributeData> attributeDataListMemoryUsed = 
			new JmxAttributesReader(".*java.lang:type=Memory.*HeapMemoryUsage/used").
					getAttributesFromLocal(this.getClass().getName());
        assertEquals(2, attributeDataListMemoryUsed.size()); 
        // 1) sun.management.MemoryImpl:java.lang:type=Memory:HeapMemoryUsage/used
        // 2) sun.management.MemoryImpl:java.lang:type=Memory:NonHeapMemoryUsage/used
        
        List<MBeanAttributeData> attributeDataListGc = 
        	new JmxAttributesReader(".*sun.management.GarbageCollector.*").
        			getAttributesFromLocal(this.getClass().getName());
        assertTrue(attributeDataListGc.size() > 0);
        
        List<MBeanAttributeData> attributeDataListMemory = 
        	new JmxAttributesReader(".*sun.management.Memory.*").
        			getAttributesFromLocal(this.getClass().getName()); // MemoryPool
        assertTrue(attributeDataListMemory.size() > 0);
        
        List<MBeanAttributeData> attributeDataListOs = 
        	new JmxAttributesReader(".*sun.management.OperatingSystem.*").
					getAttributesFromLocal(this.getClass().getName());
        assertTrue(attributeDataListOs.size() > 0);
    }
    
    public void testMBeansAttributesLVMListAllOs() throws Exception {
        List<MBeanAttributeData> attributeDataListOs = 
        	new JmxAttributesReader(".*sun.management.OperatingSystem.*").
        			getAttributesFromLocal("");
        assertTrue(attributeDataListOs.size() > 0);
    }
    
    public void testMBeansAttributesLVMSpecificObjectAttr() throws Exception {
        List<MBeanAttributeData> attributeDataListOs = 
        	new JmxAttributesReader("java.lang:type=Memory", "HeapMemoryUsage", "used").
        			getAttributesFromLocal(this.getClass().getName());
        assertEquals(1, attributeDataListOs.size());
    }
    
    public void testMBeansAttributesRemoteListAllOs() throws Exception {
        List<MBeanAttributeData> attributeDataListOs = 
        	new JmxAttributesReader(".*sun.management.OperatingSystem.*").
        			getAttributesFromHost("localhost", 9999);
        assertTrue(attributeDataListOs.size() > 0);
    }
    
    public void testMBeansAttributesRemoteSpecificObjectAttr() throws Exception {
        List<MBeanAttributeData> attributeDataListOs = 
        	new JmxAttributesReader("java.lang:type=Memory", "HeapMemoryUsage", "used").
        			getAttributesFromHost("localhost", 9999);
        assertEquals(1, attributeDataListOs.size());
    }
}
