package example.ds;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;

import java.util.concurrent.TimeUnit;

/**
 * User: akondratyev
 */
public class Peer {

    public static void main(String[] args) throws Exception {
        Cache cache = new CacheFactory()
                .create();

        while(Thread.currentThread().isAlive())
            TimeUnit.SECONDS.sleep(1);

        cache.close();
    }
}
