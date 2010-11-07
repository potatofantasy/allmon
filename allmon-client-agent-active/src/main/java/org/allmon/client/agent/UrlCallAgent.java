package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.allmon.common.AllmonCommonConstants;
import org.allmon.common.AllmonPropertiesConstants;
import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This agent can call a health check implementation described by an url. 
 * 
 */
public class UrlCallAgent extends ActiveAgent {

	private static final Log logger = LogFactory.getLog(UrlCallAgent.class);
    
    protected String urlAddress; // = "http://www.google.com";
    protected String searchPhrase; // TODO potentially searchPrase can be a more complex object
    protected boolean useProxy = true;
    
    protected String checkingHost;
    protected String checkName;
    protected String instanceName;
    
    
    public UrlCallAgent(AgentContext agentContext) {
    	super(agentContext);
    	logger.debug("Instance created");
	}

    MetricMessageWrapper collectMetrics() {
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
        MetricMessage metricMessage = MetricMessageFactory.createUrlCallMessage(
                urlAddress, instanceName, searchPhrase, metricValue, null);

        return new MetricMessageWrapper(metricMessage);
    }
    
    protected URLConnection makeConnection() throws IOException {
        logger.debug("establishing connection to: " + urlAddress);
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
            logger.debug(AllmonPropertiesConstants.ALLMON_CLIENT_AGENT_PROXY_ACTIVE + " is set to not active - proxy authorization won't be proceded");
            return;
        }
        
        logger.debug("establilishing connection using proxy: " + AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_HOST + ":" + AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_PORT);
        
        // First set the Proxy settings on the System
        Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_HOST);
        systemSettings.put("http.proxyPort", AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_PORT);
        
        // prepare the proxy authorization String  
        String userPassword = AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_USERNAME + ":" + AllmonCommonConstants.ALLMON_CLIENT_AGENT_PROXY_PASSWORD;
        //String encodedUserPassword = new sun.misc.BASE64Encoder().encode(userPassword.getBytes()); // Sun proprietary API
        String encodedUserPassword = new String(new org.apache.commons.codec.binary.Base64().encode(userPassword.getBytes()));
        
        // get authorization from the proxy
        connection.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPassword);
    }
    
//    void decodeAgentTaskableParams() throws Exception {
//        urlAddress = getParamsString(0);
//        searchPhrase = getParamsString(1);
//    }

	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}

	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public void setCheckingHost(String checkingHost) {
		this.checkingHost = checkingHost;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

}
