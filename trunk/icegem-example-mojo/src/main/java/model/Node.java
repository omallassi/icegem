package model;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import model.buildings.Building;
import model.transport.Car;

/**
 * User: akondratyev
 */
public class Node {
    public static void main(String[] args) throws Exception {
        Cache cache = new CacheFactory()
                .set("locators", "localhost[10335]")
                .set("mcast-port", "10221")
                .create();
        Region r = cache.getRegion("test");

        System.out.println("waiting for input");
        HierarchyRegistry.registerAll(Node.class.getClassLoader(), Car.class, Building.class);

        Building b = new Building("building");
        Car c = new Car(12);
        System.out.println("put data");

        r.put(1, b);
        r.put(2, c);


        boolean flag = true;
        while (flag)
            Thread.sleep(1000);

        cache.close();
    }
}
