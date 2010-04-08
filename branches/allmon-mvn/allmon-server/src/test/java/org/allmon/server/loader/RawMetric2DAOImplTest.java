package org.allmon.server.loader;

import junit.framework.TestCase;

public class RawMetric2DAOImplTest extends TestCase {

    private RawMetric2DAOImpl dao = new RawMetric2DAOImpl();
    
    public void testConcurrentThreadsCount() {
        dao.setMaxConcurrentThreadsCount(10);
        dao.setPreferedMetricsCountPerThread(10);
        
        assertEquals(1, dao.getConcurrentThreadsCount(1));
        assertEquals(1, dao.getConcurrentThreadsCount(2));
        // ...
        assertEquals(1, dao.getConcurrentThreadsCount(9));
        assertEquals(2, dao.getConcurrentThreadsCount(10));
        assertEquals(2, dao.getConcurrentThreadsCount(11));
        // ...
        assertEquals(2, dao.getConcurrentThreadsCount(19));
        assertEquals(3, dao.getConcurrentThreadsCount(20));
        assertEquals(3, dao.getConcurrentThreadsCount(21));
        // ...
        assertEquals(3, dao.getConcurrentThreadsCount(29));
        assertEquals(4, dao.getConcurrentThreadsCount(30));
        assertEquals(4, dao.getConcurrentThreadsCount(31));
        // ...
        assertEquals(10, dao.getConcurrentThreadsCount(98));
        assertEquals(10, dao.getConcurrentThreadsCount(99));
        assertEquals(10, dao.getConcurrentThreadsCount(100));
        assertEquals(10, dao.getConcurrentThreadsCount(101));
        assertEquals(10, dao.getConcurrentThreadsCount(110));
        assertEquals(10, dao.getConcurrentThreadsCount(1000));
        assertEquals(10, dao.getConcurrentThreadsCount(2000));
    }

}
