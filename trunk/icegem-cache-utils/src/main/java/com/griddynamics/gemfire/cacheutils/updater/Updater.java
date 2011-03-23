package com.griddynamics.gemfire.cacheutils.updater;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

import javassist.CannotCompileException;

import com.gemstone.gemfire.cache.Region;
import com.griddynamics.gemfire.serialization.HierarchyRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates entries in given regions.
 */
public class Updater {
	private static final Logger log = LoggerFactory.getLogger(Updater.class);
    private CountDownLatch done;

	public void updateRegions(Set<Region<?, ?>> regions) {
        registerSerializers();
        done = new CountDownLatch(regions.size());
		ExecutorService executor = Executors.newFixedThreadPool(regions.size());
		for (Region<?, ?> region : regions)
			executor.execute(new UpdateRunner(region));
        try {
            done.await();
        } catch (InterruptedException e) {
            log.info("Some error ocurred. Will stop updating." + e.getMessage());
        }
    }

	// TODO: make this automatically for all classes in specified jar file with domain objects
	private static void registerSerializers() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		List<Class<?>> classesFromPackages = new ArrayList<Class<?>>();
		//classesFromPackages.add(Company.class);
		//classesFromPackages.add(Product.class);

		try {
			HierarchyRegistry.registerAll(classLoader, classesFromPackages);
		} catch (InvalidClassException e) {
			log.error(e.getMessage());
		} catch (CannotCompileException ex) {
			log.error(ex.getMessage());
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
				}
				log.info("Update of region " + region + " successful");
			} catch (Throwable t) {
				log.info("Update of region " + region + " failed");
				log.error("Exception occured in region " + region + "\n"
						+ t.getMessage());
			} finally {
			    done.countDown();
			}
		}
	}

}
