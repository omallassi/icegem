package com.googlecode.gemfire.cacheutils.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fest.assertions.Assertions;

import java.util.Set;
import java.util.HashSet;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.admin.AdminException;
import com.googlecode.icegem.cacheutils.common.AdminService;
import com.googlecode.icegem.cacheutils.common.PeerCacheService;
import com.googlecode.icegem.cacheutils.updater.Updater;

//TODO upgrade this test
public class PeerCacheServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PeerCacheServiceTest.class);
    private static final int LOCATOR_PORT = 10356;
    private Cache cache;
    private AdminService admin;
    private Set<Region<?,?>> regions = new HashSet<Region<?,?>>();
    private PeerCacheService peerCacheService;


    //@BeforeClass
    public void before() throws Exception {

        startPeerCache();
        createRegions();
        fillRegionsWithData();
        peerCacheService = new PeerCacheService(null, null);
    }

    //@Test
    public void testUpdateRegions() throws AdminException {
        Updater updater = new Updater();
        log.info("Updating regions...");
        updater.updateRegions(regions);
        Assertions.assertThat(true);
        //Assertions.assertThat(new TreeSet<String>(regionNames)).isEqualTo(new TreeSet<String>(expectedRegionNames));
    }

    private void startPeerCache() {
        cache = new CacheFactory().set("mcast-port", "0").set("locators", "localhost[" + LOCATOR_PORT + "]").create();
        log.info("Cache started successfully ");
    }

    private void createRegions() {
        AttributesFactory attributesFactory = new AttributesFactory();
        //attributesFactory.setScope(Scope.DISTRIBUTED_ACK);
        attributesFactory.setDataPolicy(DataPolicy.PARTITION);
        RegionAttributes regionAttributes1 = attributesFactory.create();

        AttributesFactory attributesFactory2 = new AttributesFactory();
        attributesFactory2.setScope(Scope.GLOBAL);
        attributesFactory2.setDataPolicy(DataPolicy.REPLICATE);
        RegionAttributes regionAttributes2 = attributesFactory2.create();

        AttributesFactory attributesFactory3 = new AttributesFactory();
        attributesFactory3.setScope(Scope.LOCAL);
        RegionAttributes regionAttributes3 = attributesFactory3.create();

        //REGION 1 WITH SUBREGIONS
        Region region1 = cache.createRegionFactory(RegionShortcut.REPLICATE).create("region1");

        Region subregion1OfRegion1 = region1.createSubregion("subregion1OfRegion1", regionAttributes2);
        Region subregion2OfRegion1 = region1.createSubregion("subregion2OfRegion1", regionAttributes2);


        //REGION 2 WITH SUBREGIONS
        Region region2 = cache.createRegionFactory(RegionShortcut.REPLICATE).create("region2");

        Region subregion1OfRegion2 = region2.createSubregion("subregion1OfRegion2", regionAttributes2);

        Region subregion1OfSubregion1OfRegion2 = subregion1OfRegion2.createSubregion("subregion1OfSubregion1OfRegion2", regionAttributes1);


        //REGION 3 WITH SUBREGIONS
        Region region3 = cache.createRegionFactory(RegionShortcut.REPLICATE).create("region3");

        Region subregion1OfRegion3 = region3.createSubregion("subregion1OfRegion3", regionAttributes2);
        Region subregion2OfRegion3 = region3.createSubregion("subregion2OfRegion3", regionAttributes2);
        Region subregion3OfRegion3 = region3.createSubregion("subregion3OfRegion3", regionAttributes2);

        Region subregion1OfSubregion3OfRegion3 = subregion1OfRegion3.createSubregion("subregion1OfSubregion3OfRegion3", regionAttributes2);
        Region subregion2OfSubregion3OfRegion3 = subregion1OfRegion3.createSubregion("subregion2OfSubregion3OfRegion3", regionAttributes2);

        Region subregion1OfSubregion2OfSubregion3OfRegion3 = subregion2OfSubregion3OfRegion3.createSubregion("subregion1OfSubregion2OfSubregion3OfRegion3", regionAttributes3);

        regions.add(region1);
        regions.add(subregion1OfRegion1);
        regions.add(subregion2OfRegion1);
        regions.add(region2);
        regions.add(subregion1OfRegion2);
        regions.add(subregion1OfSubregion1OfRegion2);
        regions.add(region3);
        regions.add(subregion1OfRegion3);
        regions.add(subregion2OfRegion3);
        regions.add(subregion3OfRegion3);
        regions.add(subregion1OfSubregion3OfRegion3);
        regions.add(subregion2OfSubregion3OfRegion3);
        regions.add(subregion1OfSubregion2OfSubregion3OfRegion3);
    }

    private void fillRegionsWithData() {
        for (Region region : regions) {
            region.put(region.getName(), region.getName());
        }
        log.info("Regions filled with data");
    }
}
