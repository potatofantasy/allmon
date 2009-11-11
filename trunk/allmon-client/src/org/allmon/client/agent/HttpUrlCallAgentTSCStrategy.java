package org.allmon.client.agent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;

public class HttpUrlCallAgentTSCStrategy extends HttpUrlCallAgentAbstractStrategy {
    
    private OutputParser parser;
    
    
    MetricMessageWrapper extractMetrics() {
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        
        parser = new OutputParser(bufferedReaderCallResponse);
        
//        #STATUS=1
        metricMessageWrapper.add(getMetricMessageWithValue("STATUS"));
//        #USERS=292
        metricMessageWrapper.add(getMetricMessageWithValue("USERS"));
//        #EJBS=96
        metricMessageWrapper.add(getMetricMessageWithValue("EJBS"));
//        #EJBS_RES=61
        metricMessageWrapper.add(getMetricMessageWithValue("EJBS_RES"));
//        #EJBS_VL=35
        metricMessageWrapper.add(getMetricMessageWithValue("EJBS_VL"));
//        #EJBS_MMD=0
        metricMessageWrapper.add(getMetricMessageWithValue("EJBS_MMD"));
//        #TIME_JCLCWEB=21
        metricMessageWrapper.add(getMetricMessageWithValue("TIME_JCLCWEB"));
//        #TIME_JCLCEJB=20
        metricMessageWrapper.add(getMetricMessageWithValue("TIME_JCLCEJB"));
//        #TIME_DBSIMPLE=6
        metricMessageWrapper.add(getMetricMessageWithValue("TIME_DBSIMPLE"));
//        #TIME_DBCOMPLEX=537
        metricMessageWrapper.add(getMetricMessageWithValue("TIME_DBCOMPLEX"));
//        #CRTTIME=20091105_160640
        
        return metricMessageWrapper;
    }

    private MetricMessage getMetricMessageWithValue(String subcheckName) {
        String foundPhrase = parser.findFirst("#" + subcheckName + "=\\d+");
        long metricValue = 0;
        if (!foundPhrase.trim().equals("")) {
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(foundPhrase);
            if (m.find()) {
                CharSequence cs = m.group();
                metricValue = Long.parseLong(cs.toString());
            }
        }
        return MetricMessageFactory.createURLCallMessage(
                agent.checkName + "-" + subcheckName, agent.checkingHost, metricValue);
    }

}
