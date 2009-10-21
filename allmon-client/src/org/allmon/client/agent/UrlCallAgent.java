package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent can call a health check implementation described by an url. 
 * 
 */
public class UrlCallAgent extends ActiveAgent {

    private static final Log logger = LogFactory.getLog(UrlCallAgent.class);
    
    protected String urlAddress; // = "http://www.google.com";
    protected String searchPhrase;
    protected boolean useProxy = true;

    MetricMessage collectMetrics() {
        String metric = "0";
        
        try {
            URLConnection connection = makeConnection();
            //DataInputStream dis = new DataInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            metric = OutputParser.findFirst(br, searchPhrase);
            br.close();
        } catch (MalformedURLException me) {
            //fullSearchResults.append(me.getMessage());
            logger.debug("MalformedURLException: " + me, me);
        } catch (IOException ioe) {
            //fullSearchResults.append(ioe.getMessage());
            logger.debug("IOException: " + ioe, ioe);
        }
        
        double metricValue = Double.parseDouble(metric);
        MetricMessage metricMessage = MetricMessageFactory.createURLCallMessage(
        		urlAddress, searchPhrase, metricValue);
        return metricMessage;
    }
    
    protected URLConnection makeConnection() throws IOException {
        URL url = new URL(urlAddress);
        URLConnection connection = url.openConnection();
        if (useProxy) {
            ifProxySetAuthorize(connection);
        }
        return connection;
    }
    
    protected void ifProxySetAuthorize(URLConnection connection) {
        if (!AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_ACTIVE) {
            // proxy authorization is not active - is not set in properties
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
    
    void decodeAgentTaskableParams() {
        urlAddress = getParamsString(0);
        searchPhrase = getParamsString(1);
    }

}
