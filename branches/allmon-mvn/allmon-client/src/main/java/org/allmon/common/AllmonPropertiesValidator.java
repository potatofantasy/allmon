package org.allmon.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AllmonPropertiesValidator {

    private static final Log logger = LogFactory.getLog(AllmonPropertiesValidator.class);
    
    public boolean validateMandatoryProperties() {
        boolean validationResult = true;
        String instance = AllmonPropertiesReader.getInstance().getValue(AllmonPropertiesConstants.ALLMON_CLIENT_INSTANCE_NAME);
        if ("".equals(instance)) {
            logger.fatal("Mandatory property " + AllmonPropertiesConstants.ALLMON_CLIENT_INSTANCE_NAME + " has not been speciefied in allmon.properties file");
            validationResult = false;
        }
        return validationResult;
    }
  
    public boolean validateSuggestedProperties() {
        boolean validationResult = true;
        // TODO add validation logic
        return validationResult;
    }
    
}
