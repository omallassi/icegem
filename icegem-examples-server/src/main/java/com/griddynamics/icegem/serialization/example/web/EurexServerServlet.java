package com.griddynamics.icegem.serialization.example.web;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.griddynamics.icegem.serialization.example.bean.Company;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author igolovach
 */

public class EurexServerServlet extends HttpServlet {

    @Override
    public void init(final ServletConfig config) throws ServletException {

        Cache cache = new CacheFactory()
                .set("cache-xml-file", "EurexServer.xml")
                .create();

        final Region<String, Company> eurexRegion = cache.getRegion("Eurex-Region");

        // Register class in GemFire
//        try {
//            HierarchyRegistry.register(Company.class);
//        } catch (NotFoundException e) {
//            throw new ServletException(e);
//        } catch (CannotCompileException e) {
//            throw new ServletException(e);
//        }

//        // Register classes in GemFire
//        new ClassPathXmlApplicationContext("applicationContext.xml");

        Thread thread = new Thread(new Runnable() {
            private final AtomicInteger counter = new AtomicInteger(0);

            public void run() {
                while (true) {
                    int index = counter.incrementAndGet();
                    try {
                        Thread.sleep(Integer.parseInt(config.getInitParameter("put-interval")));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    Company company = WebUtils.produceNextCompany();
                    final String key = "Key-" + index;
                    eurexRegion.put(key, company);
                    System.out.println("\n----------------------------");
                    System.out.println("PUTTING ENTRY: \nKEY:" + key + "\nVALUE:\n" + company);
                }
            }
        });
//        thread.setDaemon(true);
        thread.start();
    }
}

