package itest.com.googlecode.icegem.serialization;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;
import itest.com.googlecode.icegem.serialization.model.Laptop;
import javassist.CannotCompileException;
import org.fest.assertions.Assertions;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.concurrent.TimeoutException;

/**
 * TODO: Disabled because of unstable work.
 * Tests for checking serialization/deserialization from client.
 *
 * @author Andrey Stepanov aka standy
 */
public class SerializationDeserializationClientTest {
    /** Field LOCATOR_PORT  */
    private static final int LOCATOR_PORT = 10355;
    /** Field cache  */
    private static ClientCache cache;
    /** Field region  */
    private static Region<String, Laptop> region;
    /** Field cacheServer1  */
    private static Process cacheServer1;
    /** Field cacheServer2  */
    private static Process cacheServer2;
    /** Field javaProcessLauncher  */
    private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

//    @BeforeClass
    public void setUp() throws IOException, InterruptedException, TimeoutException {
        startCacheServers();
        startClient();
    }

//    @AfterClass
    public void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

//    @Test(expectedExceptions = SerializationException.class)
    public void testSerializationDeserializationOfTheClassWithoutRegisteredDataSerializerForReplicatedRegion() throws InvalidClassException, CannotCompileException, InterruptedException {
        Laptop laptop = new Laptop(123, "GemFire");
        region.create("1", laptop);
    }

//    @Test(dependsOnMethods = "testSerializationDeserializationOfTheClassWithoutRegisteredDataSerializerForReplicatedRegion")
    public void testSerializationDeserializationOfTheClassWithRegisteredDataSerializer() throws InvalidClassException, CannotCompileException, InterruptedException {
        HierarchyRegistry.registerAll(SerializationDeserializationServerTest.class.getClassLoader(), Laptop.class);
        Laptop laptop = new Laptop(123, "GemFire");

        region.create("1", laptop);

        Laptop storedLaptop = region.get("1");
        Assertions.assertThat(storedLaptop.getId()).as("Information about company was not saved").isEqualTo(laptop.getId());
        Assertions.assertThat(storedLaptop.getName()).as("Information about company was not saved").isEqualTo(laptop.getName());
    }

    /**
     * Starts a client.
     */
    private void startClient() {
        ClientCacheFactory clientCacheFactory = new ClientCacheFactory().addPoolLocator("localhost", LOCATOR_PORT);
        cache = clientCacheFactory.set("log-level", "warning").create();
        ClientRegionFactory<String, Laptop> regionFactory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        region = regionFactory.create("data");
    }

    /**
     * Starts two cache servers for tests.
     *
     * @throws IOException when
     * @throws InterruptedException when
     */
    private void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=serializationDeserializationServerProperties.properties"}, null);
        cacheServer2 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=serializationDeserializationServerProperties.properties"}, null);
    }

    /**
     * Stops cache servers.
     *
     * @throws IOException when
     * @throws InterruptedException
     */
    private void stopCacheServers() throws IOException, InterruptedException {
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
        javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
    }
}
