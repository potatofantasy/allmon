package org.allmon.client.agent;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent can call 
 * 
 */
public class UrlCallAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(UrlCallAgent.class);
    
    private String urlAddress; // = "http://www.google.com";
    private String searchPhrase;
    
    public void setParameters(String[] paramsString) {
        if (paramsString != null && paramsString.length >= 2) {
            urlAddress = paramsString[0];
            searchPhrase = paramsString[1];        
        }
    }

    public MetricMessage collectMetrics() {
        StringBuffer fullSearchResults = new StringBuffer();
        String metric = "0";
        
        try {
            URLConnection connection = makeConncerion();
            
            DataInputStream dis = new DataInputStream(connection.getInputStream());

            String inputLine;
            int i = 0;
            while ((inputLine = dis.readLine()) != null) {
                logger.debug(inputLine);
                Pattern p = Pattern.compile(searchPhrase);
                Matcher m = p.matcher(inputLine);
                while (m.find()) {
                    CharSequence cs = m.group();
                    fullSearchResults.append(cs);
                    fullSearchResults.append(" ");
                    if (i == 0) {
                        metric = cs.toString();
                    }
                    i++;
                }
                fullSearchResults.append("\n");
            }
            dis.close();
            logger.debug("\nFound " + i + " phrases " + fullSearchResults.toString()); // XXX send the message
        } catch (MalformedURLException me) {
            fullSearchResults.append(me.getMessage());
            logger.debug("MalformedURLException: " + me, me);
        } catch (IOException ioe) {
            fullSearchResults.append(ioe.getMessage());
            logger.debug("IOException: " + ioe, ioe);
        }
        
        double metricValue = Double.parseDouble(metric);
        MetricMessage metricMessage = MetricMessageFactory.createURLCallMessage(urlAddress, searchPhrase, metricValue);
        return metricMessage;
    }
    
    private URLConnection makeConncerion() throws IOException {
        URL url = new URL(urlAddress);
        URLConnection connection = url.openConnection();
        ifProxySetAuthorize(connection);
        return connection;
    }
    
    private void ifProxySetAuthorize(URLConnection connection) {
        if (!AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_ACTIVE) {
            // proxy authorization is not needed
            return;
        }
        
        // First set the Proxy settings on the System
        Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_HOST);
        systemSettings.put("http.proxyPort", AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_PORT);
        
        // prepare the proxy authorization String  
        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
        String userPassword = AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_USERNAME + ":" + AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_PASSWORD;
        String encodedUserPassword = encoder.encode(userPassword.getBytes());  
        
        // get authorization from the proxy
        connection.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPassword);  
        
    }

}
