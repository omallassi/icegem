package itest.com.googlecode.icegem.serialization;

import com.gemstone.gemfire.InternalGemFireException;
import com.gemstone.gemfire.cache.*;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;
import itest.com.googlecode.icegem.serialization.model.Laptop;
import javassist.CannotCompileException;
import org.fest.assertions.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Tests for checking serialization/deserialization from server.
 *
 * @author Andrey Stepanov aka standy
 */
public class SerializationDeserializationServerTest {
    /** Field LOCATOR_PORT  */
    private static final int LOCATOR_PORT = 10355;
    /** Field cache  */
    private static Cache cache;
    /** Field region  */
    private static Region<String, Laptop> region;
    /** Field cacheServer1  */
    private static Process cacheServer1;
    /** Field cacheServer2  */
    private static Process cacheServer2;
    /** Field javaProcessLauncher  */
    private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

    @BeforeClass
    public void setUp() throws IOException, InterruptedException, TimeoutException {
        startCacheServers();
        startServer();
    }

    @AfterClass
    public void tearDown() throws IOException, InterruptedException {
        cache.close();
        stopCacheServers();
    }

    @Test(expectedExceptions = InternalGemFireException.class)
    public void testSerializationDeserializationOfTheClassWithoutRegisteredDataSerializerForReplicatedRegion() throws InvalidClassException, CannotCompileException {
        Laptop laptop = new Laptop(123, "MacBook");
        region.create("1", laptop);
    }

    @Test(dependsOnMethods = "testSerializationDeserializationOfTheClassWithoutRegisteredDataSerializerForReplicatedRegion")
    public void testSerializationDeserializationOfTheClassWithRegisteredDataSerializer() throws InvalidClassException, CannotCompileException, InterruptedException {
        HierarchyRegistry.registerAll(SerializationDeserializationServerTest.class.getClassLoader(), Laptop.class);
        Laptop laptop = new Laptop(123, "MacBook");

        region.create("1", laptop);

        Laptop storedLaptop = region.get("1");
        Assertions.assertThat(storedLaptop.getId()).as("Information about company was not saved").isEqualTo(laptop.getId());
        Assertions.assertThat(storedLaptop.getName()).as("Information about company was not saved").isEqualTo(laptop.getName());
    }

    /**
     * Starts a cache server.
     * @throws java.io.IOException
     */
    private void startServer() throws IOException {
        // TODO: Move PropertiesHelper into core module
        Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/serializationDeserializationServerProperties.properties"));

        cache = new CacheFactory()
                .set("cache-xml-file", "serializationDeserializationMainServerCache.xml")
                .set("log-level", "warning")
                .set("mcast-port", "0")
                .set("locators", "localhost[" + LOCATOR_PORT + "]")
                .set("license-file", properties.getProperty("license-file"))
                .set("license-type", properties.getProperty("license-type"))
                .create();
        region = cache.getRegion("data");
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

