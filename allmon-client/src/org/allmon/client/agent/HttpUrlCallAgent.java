package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.www.protocol.http.HttpURLConnection;

public class HttpUrlCallAgent extends UrlCallAgent {

	private static final Log logger = LogFactory.getLog(HttpUrlCallAgent.class);
    	
    private String requestMethod = "POST";
    private String contentType = "text/html"; //"application/json; charset=utf-8";
    private String urlParameters = "";
    
    private HttpUrlCallAgentAbstractStrategy strategy = new HttpUrlCallAgentBooleanStrategy(); // default strategy
    
    public HttpUrlCallAgent(AgentContext agentContext) {
		super(agentContext);
	}
    
    public void setStrategy(String strategyClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class c = Class.forName(strategyClassName);
        Object o = c.newInstance();
        if (o instanceof HttpUrlCallAgentAbstractStrategy) {
            setStrategy((HttpUrlCallAgentAbstractStrategy)o);
        }
    }
    
    public void setStrategy(HttpUrlCallAgentAbstractStrategy strategy) {
        this.strategy = strategy;
    }
    
    MetricMessageWrapper collectMetrics() {
        MetricMessageWrapper messageWrapper = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)makeConnection();
            
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", contentType);
            //"application/x-www-form-urlencoded"; "application/json; charset=utf-8";
            
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            
            connection.setUseCaches(false);
            connection.setDoInput(true);
            
            //Send request
            if ("POST".equals(requestMethod)) {
                //using "setDoOutput(true)" which forces HttpURLConnection to use POST, not GET, 
                connection.setDoOutput(true);

            } else if ("GET".equals(requestMethod)) {
                //connection.setDoOutput(false);
                connection.setDoOutput(true);
                
            } else {
                throw new RuntimeException("Not supported request method");
            }
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
                        
            //Get Response    
            //DataInputStream dis = new DataInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            // process the response depending on used strategy
            try {
                strategy.setUp(this, br);
                messageWrapper = strategy.extractMetrics();
            } finally {
                br.close();
            }
            
        } catch (Throwable t) {
            logger.debug("Throwable: " + t, t);
            messageWrapper = new MetricMessageWrapper(MetricMessageFactory.createURLCallMessage(checkName,  checkingHost, 0));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
        return messageWrapper;
    }
    
    void decodeAgentTaskableParams() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        setStrategy(getParamsString(0));
        urlAddress = getParamsString(1);
        searchPhrase = getParamsString(2);
        contentType = getParamsString(3);
        urlParameters = getParamsString(4);
        checkingHost = getParamsString(5);
        checkName = getParamsString(6);
        useProxy = Boolean.parseBoolean(getParamsString(7)); //true;
        requestMethod = getParamsString(8);
    }
    
}
