package com.griddynamics.icegem.serialization.example.console;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.griddynamics.icegem.serialization.example.bean.Company;
import com.griddynamics.icegem.serialization.example.web.WebUtils;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author igolovach
 */
public class ConsoleServer {

    public static void main(String[] args) throws NotFoundException, CannotCompileException {

        // Register class in GemFire
//        HierarchyRegistry.register(Company.class);

        new ClassPathXmlApplicationContext("applicationContext.xml");

        Cache cache = new CacheFactory()
                .set("cache-xml-file", "EurexServer.xml")
                .create();

        final Region<String, Company> eurexRegion = cache.getRegion("Eurex-Region");

        Thread thread = new Thread(new Runnable() {
            private final AtomicInteger counter = new AtomicInteger(0);

            public void run() {
                while (true) {
                    int index = counter.incrementAndGet();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    Company company = WebUtils.produceNextCompany();
                    final String key = "Key-" + index;
                    eurexRegion.put(key, company);
                    System.out.println("PUTTING ENTRY: \nKEY:" + key + "\nVALUE:\n" + company);
                }
            }
        });
//        thread.setDaemon(true);
        thread.start();
    }
}
