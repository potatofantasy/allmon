package org.allmon.common;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

public class AllmonActiveMQConnectionFactory {

    public final static ConnectionFactory client() {
//          return new ActiveMQConnectionFactory(
//                        AllmonCommonConstants.CLIENT_BROKER_USER, 
//                        AllmonCommonConstants.CLIENT_BROKER_PASSWORD, 
//                        AllmonCommonConstants.CLIENT_BROKER_URL);
        return new PooledConnectionFactory(
                new ActiveMQConnectionFactory(
                        AllmonCommonConstants.CLIENT_BROKER_USER, 
                        AllmonCommonConstants.CLIENT_BROKER_PASSWORD, 
                        AllmonCommonConstants.CLIENT_BROKER_URL));
    }

    public final static ConnectionFactory server() {
//        return new ActiveMQConnectionFactory(
//                AllmonCommonConstants.CLIENT_BROKER_USER, 
//                AllmonCommonConstants.CLIENT_BROKER_PASSWORD, 
//                AllmonCommonConstants.CLIENT_BROKER_URL);
        return new PooledConnectionFactory(
                new ActiveMQConnectionFactory(
                        AllmonCommonConstants.SERVER_BROKER_USER, 
                        AllmonCommonConstants.SERVER_BROKER_PASSWORD, 
                        AllmonCommonConstants.SERVER_BROKER_URL));
    }
    
}
