package org.allmon.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class AllmonPropertiesReader {

    private static final Log logger = LogFactory.getLog(AllmonPropertiesReader.class);

    private static AllmonPropertiesReader allmonPropertiesReader;

    private static final Properties properties = new Properties();

    private static boolean log4jConfigured = false;
    
    public static void readLog4jProperties() {
        if (!log4jConfigured) {
            String file = System.getProperty("log4jallmon.properties.path");
            if (file == null || "".equals(file)) {
                file = "conf/log4jallmon.properties";
            }
            PropertyConfigurator.configure(file);
        }
        log4jConfigured = true;
        //System.out.println("log4j.configured");
    }
    
    /**
     * This method is used to get the instance of the static class.
     * 
     * @return TropicsPropertiesReader
     */
    public static AllmonPropertiesReader getInstance() {
//        if (logger.isDebugEnabled()) {
//            logger.debug(AllmonLoggerConstants.ENTERED);
//        }

        if (allmonPropertiesReader == null) {
            allmonPropertiesReader = new AllmonPropertiesReader();

            try {
                // Get the reference to fixed list properties
                // lBundle = ResourceBundle.getBundle(allmonPropertiesReader.file);
                // String strFilePath = System.getProperty("AllmonProperties");
                // TODO check if this not should be split to client and server side
                String strFilePath = System.getProperty("allmon.properties.path");
                if (strFilePath == null || "".equals(strFilePath)) {
                    strFilePath = "conf/allmon.properties";
                }
                File file = new File(strFilePath);
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
            } catch (Exception exception) {
                if (logger.isErrorEnabled()) {
                    logger.error("Exception", exception);
                }
            }
        }

//        if (logger.isDebugEnabled()) {
//            logger.debug(AllmonLoggerConstants.EXITED);
//        }

        return allmonPropertiesReader;
    }

    /**
     * Gets the key of the value
     */
    public String getValue(String type, String defaultValue) {
        // checking and preparing parameters
        String defaultValueTrimed = null;
        if (defaultValue != null) {
            defaultValueTrimed = defaultValue.trim();
        }
        if (type == null || "".equals(type)) {
            return defaultValueTrimed;
        }
        String typeTrimed = type.trim();
        
        // searching properties file and loading properties
        String returnPropValue = null;
        if (properties.containsKey(typeTrimed)) {
            String strPropValue = properties.getProperty(typeTrimed);
            if (strPropValue != null) {
                returnPropValue = strPropValue.trim();
            } else {
                returnPropValue = defaultValueTrimed;
            }
        } else {
            returnPropValue = defaultValueTrimed;
        }

        return returnPropValue;
    }

    public String getValue(String type) {
        return getValue(type, "");
    }
    
    public int getValueInt(String type, int defaultValue) {
        String value = getValue(type);
        if (value != null && !"".equals(value)) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }
        
}