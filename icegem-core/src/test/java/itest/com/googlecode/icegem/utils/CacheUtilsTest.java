/*
 * Icegem, Extensions library for VMWare vFabric GemFire
 * 
 * Copyright (c) 2010-2011, Grid Dynamics Consulting Services Inc. or third-party  
 * contributors as indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License v3, as published by the Free Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * You should have received a copy of the GNU Lesser General Public License v3
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package itest.com.googlecode.icegem.utils;

import static com.gemstone.gemfire.cache.client.ClientRegionShortcut.PROXY;
import static junit.framework.Assert.assertEquals;
import itest.com.googlecode.icegem.AbstractIntegrationTest;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.googlecode.icegem.utils.CacheUtils;

/**
 * Tests for {@link CacheUtils} class. 
 */
public class CacheUtilsTest extends AbstractIntegrationTest {
    /** GemFire property file name. */
    private static final String PROP_FILE = "cacheUtilsTestServerProperties.properties";

    /** Partitioned region. */
    private static Region<Object, Object> partitionedRegion;

    /** Replicated region. */
    private static Region<Object, Object> replicatedRegion;

    @BeforeClass
    public static void beforeAllTests() throws Exception {
	startCacheServersAndClient(2, PROP_FILE);
	
	partitionedRegion = createClientRegion("partitioned_region", PROXY);
	replicatedRegion = createClientRegion("replicated_region", PROXY);
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
	stopCacheServersAndClient();
    }

    @After
    public void afterTest() throws InterruptedException, IOException {
	clearClientRegions();
    }

    /**
     * JUnit.
     */
    @Test
    public void testPartitionedRegionSize() {
	assertEquals(0, CacheUtils.getRegionSize(partitionedRegion));

	int keyCnt = 100;

	for (int i = 0; i < keyCnt; i++) {
	    partitionedRegion.put(i, "Value" + i);

	    if (i % 50 == 0)
		info("Stored key-value pairs: " + i);
	}

	assertEquals(keyCnt, CacheUtils.getRegionSize(partitionedRegion));
    }

    /**
     * JUnit.
     */
    @Test
    public void testReplicatedRegionSize() {
	assertEquals(0, CacheUtils.getRegionSize(replicatedRegion));

	int keyCnt = 100;

	for (int i = 0; i < keyCnt; i++) {
	    replicatedRegion.put(i, "Value" + i);

	    if (i % 50 == 0)
		info("Stored key-value pairs: " + i);
	}

	assertEquals(keyCnt, CacheUtils.getRegionSize(replicatedRegion));
    }

    /**
     * JUnit.
     */
    @Test
    public void testClearRegion() {
	/**
	 * TODO: This test is implemented in {@link RegionClearingClientTest}.
	 * 	Is it better to place it here? 
	 */
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoRetry() {
	// fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffSingeRetry() {
	// fail("Not yet implemented");
    }

    /**
     * JUnit.
     */
    @Test
    public void testRetryWithExponentialBackoffNoSuccess() {
	// fail("Not yet implemented");
    }
}
