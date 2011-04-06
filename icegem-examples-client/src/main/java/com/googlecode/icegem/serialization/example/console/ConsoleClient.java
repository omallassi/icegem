package com.googlecode.icegem.serialization.example.console;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author igolovach
 */
public class ConsoleClient {

    public static void main(String[] args) throws IOException, NotFoundException, CannotCompileException, InterruptedException {

        // Register class in GemFire
//        HierarchyRegistry.register(Company.class);

        new ClassPathXmlApplicationContext("applicationContext.xml");

        // Create the cache which causes the cache-xml-file to be parsed
        Cache cache = new CacheFactory()
                .set("cache-xml-file", "EurexClient.xml")
                .set("mcast-port", "0")
                .create();

        Thread.sleep(1000000000);
    }
}
