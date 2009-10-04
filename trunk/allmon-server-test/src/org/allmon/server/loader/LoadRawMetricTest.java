package org.allmon.server.loader;

import junit.framework.TestCase;

public class LoadRawMetricTest extends TestCase {

    public void test1() {
        try {
            LoadRawMetric l = new LoadRawMetric();
            l.loadAllmetric();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
}
