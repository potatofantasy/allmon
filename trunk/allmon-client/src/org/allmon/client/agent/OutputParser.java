package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.allmon.common.AllmonLoggerConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OutputParser {

	private static final Log logger = LogFactory.getLog(OutputParser.class);

	private ArrayList<String> buffer = new ArrayList<String>();
    
	public OutputParser(BufferedReader br) {
        setBuffer(br); 
	}
	
	private void setBuffer(BufferedReader br) {
	    logger.debug(AllmonLoggerConstants.ENTERED);
        String foundString = "";
        String inputLine;
        int i = 0;
        try {
            while ((inputLine = br.readLine()) != null) {
                buffer.add(inputLine);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
//          try {
//              br.close();
//          } catch (IOException e) {
//              logger.error(e.getMessage(), e);
//          }
        }
        logger.debug(AllmonLoggerConstants.EXITED);
	}
	
    public String findFirst(String searchPhraseRegExp) {
        logger.debug(AllmonLoggerConstants.ENTERED);
        StringBuffer fullSearchResults = new StringBuffer();
        String foundString = "";
        try {
            int f = 0;
            Pattern p = Pattern.compile(searchPhraseRegExp);
            for (int i = 0; i < buffer.size(); i++) {
                Matcher m = p.matcher(buffer.get(i));
                while (m.find()) {
                    CharSequence cs = m.group();
                    fullSearchResults.append(cs);
                    fullSearchResults.append(" ");
                    if (f == 0) {
                        foundString = cs.toString();
                        //logger.debug(AllmonLoggerConstants.EXITED);
                        //return metric; // XXX code this properly later
                    }
                    f++;
                }
                fullSearchResults.append("\n");
            }
        } finally {
        }
        //logger.debug("Found " + i + " phrases " + fullSearchResults.toString().trim());
        logger.debug(AllmonLoggerConstants.EXITED);
        return foundString;
    }

    public static String findFirst(BufferedReader br, String searchPhraseRegExp) {
        logger.debug(AllmonLoggerConstants.ENTERED);
        StringBuffer fullSearchResults = new StringBuffer();
        String foundString = "";
        String inputLine;
        int i = 0;
        try {
            while ((inputLine = br.readLine()) != null) {
                //logger.debug(inputLine);
                Pattern p = Pattern.compile(searchPhraseRegExp);
                Matcher m = p.matcher(inputLine);
                while (m.find()) {
                    CharSequence cs = m.group();
                    fullSearchResults.append(cs);
                    fullSearchResults.append(" ");
                    if (i == 0) {
                        foundString = cs.toString();
                        //logger.debug(AllmonLoggerConstants.EXITED);
                        //return metric; // XXX code this properly later
                    }
                    i++;
                }
                fullSearchResults.append("\n");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            // XXX do not closing BufferedReader in case of another search
//          try {
//              br.close();
//          } catch (IOException e) {
//              logger.error(e.getMessage(), e);
//          }
        }
        //logger.debug("Found " + i + " phrases " + fullSearchResults.toString().trim());
        logger.debug(AllmonLoggerConstants.EXITED);
        return foundString;
    }
    
}
