package org.allmon.client.agent;

import java.io.BufferedReader;

import org.allmon.common.MetricMessageWrapper;

abstract class HttpUrlCallAgentAbstractStrategy {

    protected HttpUrlCallAgent agent;
    protected BufferedReader bufferedReaderCallResponse;

    abstract MetricMessageWrapper extractMetrics();

    final void setUp(HttpUrlCallAgent agent, BufferedReader br) {
        this.agent = agent;
        this.bufferedReaderCallResponse = br;
    }

}
