package org.allmon.server.loader;

import junit.framework.TestCase;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractConcurrentLoaderTest extends TestCase {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AbstractConcurrentLoaderTest.class);
    
    private String array[] = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private int totalThreadCount = 3;
    
    public void testLoadedObjectArrayIterating() throws Exception {
        AbstractConcurrentLoader<String> loader = new AbstractConcurrentLoader<String>(array, totalThreadCount) {
            public void loadCall(String loadingObject, int thread, int iteration) throws Exception {
            }
        };

        // first iteration
        assertEquals("1", loader.getLoadingObjects(0, 0));
        assertEquals("2", loader.getLoadingObjects(0, 1));
        assertEquals("3", loader.getLoadingObjects(0, 2));
        
        // second iteration
        assertEquals("4", loader.getLoadingObjects(1, 0));
        assertEquals("5", loader.getLoadingObjects(1, 1));
        assertEquals("6", loader.getLoadingObjects(1, 2));
        
        // second iteration
        assertEquals("7", loader.getLoadingObjects(2, 0));
        assertEquals("8", loader.getLoadingObjects(2, 1));
        assertEquals("9", loader.getLoadingObjects(2, 2));
        
        // last iteration
        assertEquals("10", loader.getLoadingObjects(3, 0));
        
        // test MaxSubsequentCallsInThread
        assertEquals(3, loader.getMaxSubsequentCallsInThread());
        for (int i = 0; i < loader.getMaxSubsequentCallsInThread(); i++) {
            assertEquals(array[i * loader.getMaxSubsequentCallsInThread() + 0], loader.getLoadingObjects(i, 0));
        }
        
    }
    
    public void testBasic() throws Exception {
        AbstractConcurrentLoader<String> loader = new AbstractConcurrentLoader<String>(array, totalThreadCount) {
            public void loadCall(String loadingObject, int thread, int iteration) {
                logger.debug("thread:" + thread + ", iteration: " + iteration + " - " + loadingObject);
            }
        };
        AbstractConcurrentLoader<String>.LoadResult loadResult = loader.runLoad();
        assertEquals(0, loadResult.getFailuresCount());
//        AbstractConcurrentLoader<String>.LoadResult loadResult2 = loader.runLoad();
//        assertEquals(0, loadResult2.getFailuresCount());
        
        logger.debug("concurrent loading finished");
    }
    
    public void testExceptions() throws Exception {
        final Exception exception = new Exception("do not liked number 5 has been found");
        AbstractConcurrentLoader<String> loader = new AbstractConcurrentLoader<String>(array, totalThreadCount) {
            public void loadCall(String loadingObject, int thread, int iteration) throws Exception {
                logger.debug("thread:" + thread + ", iteration: " + iteration + " - " + loadingObject);
                if ("5".equals(loadingObject)) {
                    throw exception;
                }
            }
        };
        AbstractConcurrentLoader<String>.LoadResult loadResult = loader.runLoad();
        assertEquals(1, loadResult.getFailuresCount());
        assertEquals(1, loadResult.getExceptions().size());
        assertEquals(exception, loadResult.getException(4));
        assertEquals(1, loadResult.getProblemsLoadingObjects().size());
        assertEquals(array[4], loadResult.getProblemsLoadingObject(4));
        
        AbstractConcurrentLoader<String>.LoadResult loadResult2 = loader.runLoad();
        assertEquals(1, loadResult2.getFailuresCount());
        
        logger.debug("concurrent loading finished");
    }
 
    
    public void test2() throws Exception {
        for (int i = 0; i < array.length/totalThreadCount; i++) {
            logger.debug("i:" + i + " > " + getItem(array, i, 0, totalThreadCount));
        }
    }
    
    private String getItem(String array[], int iteration, int threadNumber, int totalThreadCount) {
        int index = iteration * totalThreadCount + threadNumber;
        return array[index];
    }
    
}
