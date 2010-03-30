package org.allmon.client.agent;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class PingAgentTest extends TestCase {
    
    private final static String LOCALHOST = "127.0.0.1";
    private final static String NOT_RECHABLE_LOCAL_HOST = "127.0.0.256";
    private final static String NOT_RECHABLE_REMOTE_HOST = "qwertyqwerty123.com";
    
    private PingAgent getPingAgent(String host) {
        PingAgent pingAgent = new PingAgent(null);
        pingAgent.setParameters(new String[] {
                host,
                "1", "1000"
        });
        pingAgent.decodeAgentTaskableParams();
        return pingAgent;
    }
    
    private PingAgent getPingAgentLocalhost() {
        return getPingAgent(LOCALHOST);
    }

    private PingAgent getPingAgentGoogle() {
        return getPingAgent("google.com");
    }

    private PingAgent getPingAgentNotExistingLocal() {
        return getPingAgent(NOT_RECHABLE_LOCAL_HOST);
    }

    private PingAgent getPingAgentNotExistingRemote() {
        return getPingAgent(NOT_RECHABLE_REMOTE_HOST);
    }
    
    public void testInetAddressIsReachable() throws Exception {
        boolean isReachable = InetAddress.getByName(LOCALHOST).isReachable(3000);
        assertTrue(isReachable);
    }

    public void testIsHostReachable() throws Exception {
        assertTrue("localhost is allways reachable", getPingAgentLocalhost().isHostReachable());
        assertTrue("google is allways reachable", getPingAgentGoogle().isHostReachable());
        
        boolean exceptionThrown = false;
        try {
            getPingAgentNotExistingLocal().isHostReachable();
        } catch (UnknownHostException uhe) {
            exceptionThrown = true;
        }
        assertTrue(NOT_RECHABLE_LOCAL_HOST + " cannot be reachable", exceptionThrown);
        
        exceptionThrown = false;
        try {
            getPingAgentNotExistingRemote().isHostReachable();
        } catch (UnknownHostException uhe) {
            exceptionThrown = true;
        }
        assertTrue(NOT_RECHABLE_REMOTE_HOST + " cannot be reachable", exceptionThrown);
    }

    public void testHostReachableTime() throws Exception {
        long time = getPingAgentLocalhost().getHostReachableTime();
        System.out.println("Host reachable check time " + time);
        assertNotSame(0, time);
        time = getPingAgentGoogle().getHostReachableTime();
        System.out.println("Host reachable check time " + time);
        assertNotSame(0, time);
    }

    public void testIsPingReachable() throws Exception {
        assertTrue("localhost is allways reachable", getPingAgentLocalhost().socketPing());
        assertTrue("google is allways reachable", getPingAgentGoogle().socketPing());
        //assertFalse(NOT_RECHABLE_LOCAL_HOST + " cannot be reachable", getPingAgentNotExistingLocal().isHostReachable());
    }

    public void testPingTime() throws Exception {
        long time = getPingAgentLocalhost().getPingTime();
        System.out.println("Ping time " + time);
        assertNotSame(0, time);
    }
    
    public void testShellPingForLocalhost() throws Exception {
        long time = getPingAgentLocalhost().shellPing();
        System.out.println("Ping time " + time);
        assertNotSame(0, time);
    }

}
