package org.allmon.common;

import junit.framework.TestCase;

public class AllmonPropertiesReaderTest extends TestCase {

    public void testReading() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.TESTVALUE);
        assertEquals(AllmonPropertiesConstants.TESTVALUE, str);
    }

    public void testReadingDeclaredButNullValue() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.TESTVALUE_NULL);
        assertEquals("", str);
    }

    public void testReadingDefaultValueIfFileValueDefined() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.TESTVALUE, "default");
        assertEquals(AllmonPropertiesConstants.TESTVALUE, str);
    }
    
    public void testReadingDefaultValueIfFileValueNotDefined() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.TESTVALUE + "_notexisting", "default");
        assertEquals("default", str);
    }

    public void testReadingNotExistingValue() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.TESTVALUE + "_notexisting");
        assertEquals("", str);
    }

    public void testReadingAllmonClientHost() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_HOST_NAME);
        assertEquals("", str);
    }

    public void testReadingAllmonClientInstance() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_INSTANCE_NAME);
        assertEquals("monitored.instance", str);
    }
    
    public void testReadingIntValue() throws Exception {
        int value =  AllmonPropertiesReader.getInstance().getValueInt(AllmonPropertiesConstants.TESTVALUE_INT, 0);
        assertEquals(123, value);
    }

    public void testReadingIntValueAndValue() throws Exception {
        String str =  AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.TESTVALUE_INT);
        int value =  AllmonPropertiesReader.getInstance().getValueInt(AllmonPropertiesConstants.TESTVALUE_INT, 0);
        assertEquals(str, String.valueOf(value));
    }
    
    public void testReadingNotExistingIntValue() throws Exception {
        int value =  AllmonPropertiesReader.getInstance().getValueInt(AllmonPropertiesConstants.TESTVALUE_INT + "_notexisting", 0);
        assertEquals(0, value);
    }
    
}
