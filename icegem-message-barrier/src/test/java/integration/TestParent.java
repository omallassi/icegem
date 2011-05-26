package integration;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.googlecode.icegem.message.barrier.core.RegionListeningBarrierBean;
import org.junit.Ignore;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * User: volcano
 */
@Ignore
public class TestParent {
    RegionListeningBarrierBean regionListeningBarrierBean;
    ClientCache client;
    Region tradeRgn;
    Region msgRgn;
    Region expiredMsgRgn;
    Region msgCheckRgn;
    Region msgSequenceRgn;
    Process process;

    public void runServerSide(Class clazz, String cacheXmlFile) throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, clazz.getCanonicalName(), cacheXmlFile);

        process = builder.start();
        TimeUnit.SECONDS.sleep(5);
        //todo: need output
        new StreamRedirector(process.getInputStream()).start();
        new StreamRedirector(process.getErrorStream()).start();
    }

    public void stopServerSide() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.newLine();
        writer.flush();
        process.destroy();
    }

    private class StreamRedirector extends Thread {
        private InputStream inputStream;

        public StreamRedirector(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(" > " + line);
                }
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
