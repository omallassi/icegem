package com.griddynamics.gemfire.cacheutils.updater;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

import com.gemstone.gemfire.cache.Region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates entries in given regions.
 */
public class Updater {
	private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private CountDownLatch done;

	public void updateRegions(Set<Region<?, ?>> regions) {
        done = new CountDownLatch(regions.size());
		ExecutorService executor = Executors.newFixedThreadPool(regions.size());
		for (Region<?, ?> region : regions)
			executor.execute(new UpdateRunner(region));
        try {
            done.await();
        } catch (InterruptedException e) {
            log.info("Some error ocurred. Will stop updating." + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

	private class UpdateRunner implements Runnable {
		private Region region;

		public UpdateRunner(Region region) {
			this.region = region;
		}

		public void run() {
			try {
				for (Object key : region.keySetOnServer()) {
					Object value = region.get(key);
					region.put(key, value);
                    //log.info("-----------------------key-value " + key + "-" + value);
				}
				log.info("Update of region " + region.getName() + " successful");
			} catch (Throwable t) {
				log.info("Update of region " + region.getName() + " failed");
				log.error("Exception occured in region " + region.getName() + "\n"
						+ t.getMessage());
			} finally {
			    done.countDown();
			}
		}
	}

}
