package org.allmon.common;

import junit.framework.TestCase;

public class AllmonPropertiesValidatorTest extends TestCase {

    public void testValidateMandatoryProperties() {
        AllmonPropertiesValidator validator = new AllmonPropertiesValidator();
        assertTrue(validator.validateMandatoryProperties());
    }
    
}
