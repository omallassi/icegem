package com.googlecode.icegem.cacheutils.latencymeasurer;

import com.gemstone.gemfire.cache.Region;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDataSender {
    private static final Logger log = LoggerFactory.getLogger(TestDataSender.class);
    private static final Long SENDING_DELAY = 1 * 1000L;
    private static final Long DEFAULT_MEASURE_HALF_FREQUENCY = 3 * 60 * 1000L;
    private long measureHalfFrequency;
    private String clusterName;

    public static Integer getDataSeriesLength() {
        return DATA_SERIES_LENGTH;
    }

    private static final Integer DATA_SERIES_LENGTH = 10;
    private Region dataRegion;
    private ExecutorService executor;
    private TestDataSenderRunner dataRunner;

    public TestDataSender(Region<?, ?> dataRegion, int measureFrequency) {
        this.dataRegion = dataRegion;
        measureHalfFrequency = measureFrequency == 0 ? DEFAULT_MEASURE_HALF_FREQUENCY : measureFrequency * 30 * 1000L;
        clusterName = (String) dataRegion.getParentRegion().get(dataRegion.getCache().getDistributedSystem().getDistributedMember().getId());
    }

    public void start() {
        dataRunner = new TestDataSenderRunner();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(dataRunner);
    }

    public void stop() {
        dataRunner.isContinue = false;
        executor.shutdown();
    }

    private class TestDataSenderRunner implements Runnable {
        private boolean isContinue = true;

        public void run() {
            while (isContinue) {
                try {
                    for (int i = 0; i < DATA_SERIES_LENGTH; i++) {
                        pushDataToServers(i);
                        Thread.sleep((SENDING_DELAY));
                    }
                    Thread.sleep((measureHalfFrequency));

                    destroyDataOnServers();
                    Thread.sleep((measureHalfFrequency));
                } catch (Throwable t) {
                    log.error(t.getMessage());
                }
            }
        }

        private void destroyDataOnServers() {
            for (int i = 0; i < DATA_SERIES_LENGTH; i++) {
                dataRegion.destroy(clusterName + "_" + i);
            }
        }

        private void pushDataToServers(int i) {
            dataRegion.put(clusterName + "_" + i, System.nanoTime());
        }
    }
}
