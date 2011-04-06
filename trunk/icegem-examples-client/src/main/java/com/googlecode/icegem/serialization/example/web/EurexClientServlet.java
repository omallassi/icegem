package com.googlecode.icegem.serialization.example.web;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author igolovach
 */
public class EurexClientServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {

        Cache cache = new CacheFactory()
                .set("cache-xml-file", "EurexClient.xml")
                .create();

        // Register class in GemFire
//        try {
//            HierarchyRegistry.register(Company.class);
//        } catch (NotFoundException e) {
//            throw new ServletException(e);
//        } catch (CannotCompileException e) {
//            throw new ServletException(e);
//        }

        // Register classes in GemFire
//        new ClassPathXmlApplicationContext("applicationContext.xml");        
    }
}

