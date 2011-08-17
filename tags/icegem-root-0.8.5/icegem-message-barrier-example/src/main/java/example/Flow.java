package example;

import com.gemstone.gemfire.cache.Region;
import example.utils.MessageGenerator;
import example.utils.MessageReceiver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.core.SubscribableChannel;

import java.util.concurrent.TimeUnit;

/**
 * User: akondratyev
 */
public class Flow {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");

        Runnable run = (Runnable) context.getBean("tradeGenerator");
        new Thread(run).start();

        MessageGenerator gen = (MessageGenerator) context.getBean("messageGenerator");
        gen.generate();

        MessageReceiver receiver = context.getBean("messageReceiver", MessageReceiver.class);

        SubscribableChannel channel = context.getBean("toExternalSystem", SubscribableChannel.class);
        channel.subscribe(receiver);

        TimeUnit.SECONDS.sleep(60);         //assume approximately program time execution

        Region messageRegion = context.getBean("messageRegion", Region.class);
        Region messageSequenceRegion = context.getBean("messageSequenceRegion", Region.class);

        System.out.println("Region: " + messageRegion.getName());
        System.out.println("KeySet size is: " + messageRegion.keySetOnServer().size());
        System.out.println("-----------------------------------");
        System.out.println("Region: " + messageSequenceRegion.getName());
        System.out.println("KeySet size is: " + messageSequenceRegion.keySet().size());
        for (Object key: messageSequenceRegion.keySet()) {
            System.out.println("on key " + key + " values " + messageSequenceRegion.get(key));
        }
    }
}
