package example;

import com.gemstone.gemfire.cache.Region;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * User: akondratyev
 */
public class Flow {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Runnable run = (Runnable) context.getBean("tradeGenerator");
        new Thread(run).start();

        TimeUnit.SECONDS.sleep(60);         //assume approximately program time execution

        Region msgRgn = context.getBean("messageRegion", Region.class);
        Region msgSequenceRgn = context.getBean("messageSequenceRegion", Region.class);

        System.out.println("Region: " + msgRgn.getName());
        System.out.println("KeySet size is: " + msgRgn.keySetOnServer().size());
        System.out.println("-----------------------------------");
        System.out.println("Region: " + msgSequenceRgn.getName());
        System.out.println("KeySet size is: " + msgSequenceRgn.keySet().size());
        for (Object key: msgSequenceRgn.keySet()) {
            System.out.println("on key " + key + " values " + msgSequenceRgn.get(key));
        }
    }
}
