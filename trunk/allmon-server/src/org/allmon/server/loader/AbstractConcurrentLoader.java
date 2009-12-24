/*
* Copyright 2009 Allmon
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package org.allmon.server.loader;

import java.util.Collection;
import java.util.HashMap;

import org.allmon.common.AllmonPropertiesReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractConcurrentLoader <LoadingObject> {

    static {
        AllmonPropertiesReader.readLog4jProperties();
    }
    
    private static final Log logger = LogFactory.getLog(AbstractConcurrentLoader.class);
    
    private final LoadingObject[] loadingObjects;
    private final int threadsCount;
    private final int maxSubsequentCallsInThread;
    
    public AbstractConcurrentLoader(LoadingObject[] loadingObjects, int threadsCount) {
        if (loadingObjects == null || loadingObjects.length == 0) {
            throw new IllegalArgumentException("loadingObjects table is null or empty");
        }
        this.loadingObjects = loadingObjects;
        this.threadsCount = threadsCount;
        this.maxSubsequentCallsInThread = loadingObjects.length/threadsCount;
    }
    
    /**
     * First method in an iteration
     * 
     * @param thread
     * @param iteration
     * @param initParameters
     * @return
     */
    abstract void loadCall(LoadingObject loadingObject, int thread, int iteration) throws Exception;
    
    
    private class LoadingThread implements Runnable {
        
        private int threadNum;
        private LoadResult loadResult;
        
        public LoadingThread(int threadNum, LoadResult loadResult) {
            this.threadNum = threadNum;
            this.loadResult = loadResult;
        }
        
        public void run() {
            logger.debug("Thread " + threadNum + " started");
            long t0 = System.nanoTime();
            
            long t1 = System.nanoTime();
            for (int i = 0; i <= maxSubsequentCallsInThread; i++) {
                int index = getLoadingObjectsIndex(i, threadNum);
                LoadingObject loadingObject = getLoadingObjects(i, threadNum);
                if (loadingObject != null) {
                    try {
                        loadCall(loadingObject, threadNum, i);
                        loadResult.addSuccess(index);
                    } catch (Exception e) {
                        //logger.error(e.getMessage(), e);
                        loadResult.addFailure(index, e, loadingObject);
                    }
                }
            }
            long t2 = System.nanoTime();
            
            logger.debug("Thread " + threadNum + " run initialized in " + (t1 - t0)/1000000);
            logger.debug("Thread " + threadNum + " run pre processed in " + (t2 - t1)/1000000);
            logger.debug("Thread " + threadNum + " run end");
        }
        

    }

    int getMaxSubsequentCallsInThread() {
        return maxSubsequentCallsInThread;
    }
    
    int getLoadingObjectsIndex(int iteration, int threadNum) {
        return iteration * threadsCount + threadNum;
    }
    
    LoadingObject getLoadingObjects(int iteration, int threadNum) {
        int index = getLoadingObjectsIndex(iteration, threadNum);
        if (index < loadingObjects.length) {
            return loadingObjects[index];
        }
        return null;
    }
    
    public class LoadResult {
        
        private final boolean loadResults[] = new boolean[loadingObjects.length];
        private final HashMap<Integer, Exception> loadExceptions = new HashMap<Integer, Exception>();
        private final HashMap<Integer, LoadingObject> loadProblemsLoadingObjects = new HashMap<Integer, LoadingObject>();
        
        void addSuccess(int index) {
            loadResults[index] = true;
        }
        
        void addFailure(int index, Exception exception, LoadingObject loadingObject) {
            loadResults[index] = false;
            loadExceptions.put(index, exception);
            loadProblemsLoadingObjects.put(index, loadingObject);
        }
        
        public int getFailuresCount() {
            int count = 0;
            for (boolean result : loadResults) {
                if (!result) {
                    count++;
                }
            }
            return count;
        }
        
        public Exception getException(int index) {
            return loadExceptions.get(index);
        }

        public Collection<Exception> getExceptions() {
            //return loadExceptions.values().toArray(new Exception[0]);
            return loadExceptions.values();
        }
        
        public LoadingObject getProblemsLoadingObject(int index) {
            return loadProblemsLoadingObjects.get(index);
        }

        public Collection<LoadingObject> getProblemsLoadingObjects() {
            return loadProblemsLoadingObjects.values();
        }
        
    }
    
    /**
     * Method executes the concurrent loading process.
     * 
     */
    public final LoadResult runLoad() throws InterruptedException {
        logger.info("runLoad - start");
        
        logger.info("runLoad - load will execute " + loadingObjects.length + " calls in " + threadsCount + " independent threads");
        logger.info("runLoad - max calls per thread: " + maxSubsequentCallsInThread);
        //logger.info("runLoad - rump up period is (all thread should run in): " + startingTimeMills + "ms");
        //logger.info("runLoad - active part of load test should take: " + ((double)maxSleepBetweenPreAndPostCall / 2 * subsequentCallsInThread / 1000) + "sec");
        //logger.info("runLoad - whole load test should take: " + (((double)sleepAfterTest + maxSleepBetweenPreAndPostCall / 2 * subsequentCallsInThread) / 1000) + "sec");
        
        final LoadResult loadResult = new LoadResult();
        
        HashMap<Integer, Thread> loadThreadsMap = new HashMap<Integer, Thread>();
        
        long t0 = System.currentTimeMillis();
        
        logger.info("runLoad - creating definitions of threads");
        for (int i = 0; i < threadsCount; i++) {
            // creating a thread
            Thread t = new Thread(new LoadingThread(i, loadResult));
            loadThreadsMap.put(new Integer(i), t);
        }
        
        logger.info("runLoad - running created threads");
        for (int i = 0; i < threadsCount; i++) {
            // taking a thread definition to run it
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.start();
        }
        
        logger.info("runLoad - waiting for running threads to finish");
        for (int i = 0; i < threadsCount; i++) {
            Thread t = (Thread)loadThreadsMap.get(new Integer(i));
            t.join();
        }

        logger.info("runLoad - load took: " + ((double)System.currentTimeMillis() - t0)/1000 + "sec");
        
        int failuresCount = loadResult.getFailuresCount();
        if (failuresCount == 0) {
            logger.info("runLoad - all loading processes threads finished sucessfully");
        } else {
            logger.warn("runLoad - some of loading processes threads finished with exceptions, exceptions count is " + failuresCount);
        }
        
        logger.info("runLoad - end");
        
        return loadResult;
    }
    
}