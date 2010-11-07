package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.allmon.common.MetricMessage;
import org.allmon.common.MetricMessageFactory;
import org.allmon.common.MetricMessageWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides simple ping check to a host.
 * 
 * TODO introduce separate strategies classes for different ping methods
 * 
 */
public class PingAgent extends ActiveAgent  {

	private static final Log logger = LogFactory.getLog(PingAgent.class);
    
    private String pingedHost = "google.com";
    private int pingCount = 1;
    private int pingDelay = 1000;
    private int timeOut = 3000;
    
    public PingAgent(AgentContext agentContext) {
        super(agentContext);
    }
    
    MetricMessageWrapper collectMetrics() {
        
        MetricMessageWrapper metricMessageWrapper = new MetricMessageWrapper();
        
        for (int i = 0; i < pingCount; i++) {
            Exception ex = null;
            long t0 = System.currentTimeMillis();
            boolean isReachable = false;
            
            try {
                isReachable = isHostReachable();
            } catch (Exception e) {
                ex = e;
            }
            
            long time = System.currentTimeMillis() - t0;
            
            MetricMessage metricMessage = MetricMessageFactory.createPingMessage(getAgentContextName(), pingedHost, time, ex);
            metricMessageWrapper.add(metricMessage);
            
            if (pingCount > 1) {
                try {
                    Thread.sleep(pingDelay);
                } catch (InterruptedException e) {
                }
            }
        }
        
        return metricMessageWrapper;
    }

    boolean isHostReachable() throws UnknownHostException, IOException {
        return InetAddress.getByName(pingedHost).isReachable(timeOut);
    }
    
    boolean socketPing() throws UnknownHostException, IOException {
        boolean isReachable = false;
        Socket t = null;
        try {
            // on a server the echo port is always port 7
            t = new Socket(pingedHost, 7); 
            // send string to that port and the server will echo the string
            DataInputStream dis = new DataInputStream(t.getInputStream());
            PrintStream ps = new PrintStream(t.getOutputStream());
            ps.println("PingMessage");
            String str = dis.readLine();
            if (str.equals("PingMessage")) {
                System.out.println("Alive!");
                isReachable = true;
            } else {
                System.out.println("Dead or echo port not responding");
                isReachable = false;
            }
        } finally {
            if (t != null) {
                t.close();
            }
        }
        return isReachable;
    }
    
    long shellPing() throws IOException {
        String shellPing = "ping " + pingedHost;
        logger.debug("Executing shell command: [" + shellPing + "]...");
        Process p = Runtime.getRuntime().exec(shellPing);
        //p.waitFor();
        logger.debug("Shell command has been executed successfully.");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        String pingTimeString = OutputParser.findFirst(br, "[0-9]+ *ms +TTL"); // TODO phrase depends on OS
        
        long pingTime = Long.parseLong(pingTimeString.split("ms")[0]);
        
        return pingTime;
    }
    
    long getHostReachableTime() throws UnknownHostException, IOException {
        long t0 = System.currentTimeMillis();
        boolean isReachable = isHostReachable();
        long time = System.currentTimeMillis() - t0;
        System.out.println("Ping time " + time);
        return isReachable ? time : 0;
    }
    
    long getPingTime() throws UnknownHostException, IOException {
        long t0 = System.currentTimeMillis();
        boolean isReachable = socketPing();
        long time = System.currentTimeMillis() - t0;
        System.out.println("Ping time " + time);
        return isReachable ? time : 0;
    }
    
//    void decodeAgentTaskableParams() {
//        pingedHost = getParamsString(0);
//        pingCount = Integer.parseInt(getParamsString(1));
//        pingDelay = Integer.parseInt(getParamsString(2));
//    }
    
}