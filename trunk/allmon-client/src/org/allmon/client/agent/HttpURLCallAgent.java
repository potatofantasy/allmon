package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.www.protocol.http.HttpURLConnection;

public class HttpURLCallAgent extends UrlCallAgent {

    private static final Log logger = LogFactory.getLog(HttpURLCallAgent.class);
    
    private String contentType = "application/json; charset=utf-8";
    private String urlParameters = "{ 'componentChecker': 'TTC.iTropics.ComponentCheckers.TropicsDawsComponentChecker, TTC.iTropics.ComponentCheckers' }";
    
    MetricMessage collectMetrics() {
        String metric = "0";
        
        try {
            HttpURLConnection connection = (HttpURLConnection)makeConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            //"application/x-www-form-urlencoded"; "application/json; charset=utf-8";
            
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            
            //Get Response    
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
    
}
