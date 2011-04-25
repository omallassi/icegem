package com.googlecode.icegem.cacheutils.latencymeasurer.threeclustertest;



import com.gemstone.gemfire.cache.CacheExistsException;
import com.gemstone.gemfire.cache.CacheWriterException;
import com.gemstone.gemfire.cache.GatewayException;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionExistsException;
import com.gemstone.gemfire.cache.TimeoutException;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.cacheutils.common.DaemonThreadFactory;
import java.lang.InterruptedException;
import java.lang.Long;
import java.lang.Thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

public  class  ClustersClient {

        private static final Long PUSHING_DELAY = 500L;

        public static void main(String[] args) throws InterruptedException {
            //registerSerializers();
            ClientCache cache = null;
            try {
                
            cache = new ClientCacheFactory().set("license-type", "development").set("license-file", "/home/yaidarova/Downloads/gemfireLicense.zip").addPoolLocator("localhost",18081).create();
            Region region = cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("data");
            ExecutorService threadExecutor = Executors.newFixedThreadPool(1, new DaemonThreadFactory());
            threadExecutor.execute(new ClientRunner(region));
            Thread.sleep(PUSHING_DELAY * 2 * 100);
            } catch (CacheExistsException e) {
                  e.printStackTrace();
            } catch (GatewayException e) {
                  e.printStackTrace();
            } catch (RegionExistsException e) {
                  e.printStackTrace();
            } catch (CacheWriterException e) {
                  e.printStackTrace();
            } catch (TimeoutException e) {
                  e.printStackTrace();
            }



        }

        private static void registerSerializers() {

        }

        private static class ClientRunner implements Runnable {
            private int bulkSize = 1000;
            private Region region;
            private int count = 0;

            public ClientRunner(Region region) {
                this.region = region;
            }

            public void run() {
                int i = 0;
                while (i < 1000) {
                    try {
                        pushDataToServers();
                        //Thread.sleep((long) (PUSHING_DELAY * Math.random()));
                        i++;
                    } catch (Throwable t) {
                        System.err.println(t.getMessage());
                        //log.error(t.getMessage());
                    }
                }
            }

            private void pushDataToServers() {
                int size = (int) (Math.random() * bulkSize);
                Map map = new HashMap(size);
                for (int i = 0; i < size; i++) {
                    map.put(count, count);
                    region.put(count + i, map);
                }
                count += size;
                //System.out.println("count --------------- " + count);
            }
        }
    }
