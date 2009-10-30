package org.allmon.client.agent;

import java.io.BufferedReader;

import org.allmon.common.MetricMessageWrapper;

abstract class HttpUrlCallAgentAbstractStrategy {

    protected UrlCallAgent agent;
    protected BufferedReader brCallResponse;

    abstract MetricMessageWrapper extractMetrics();

    final void setUp(UrlCallAgent agent, BufferedReader br) {
        this.agent = agent;
        this.brCallResponse = br;
    }

}
