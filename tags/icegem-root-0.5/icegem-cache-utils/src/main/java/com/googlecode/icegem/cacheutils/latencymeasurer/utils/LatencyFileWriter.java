package com.googlecode.icegem.cacheutils.latencymeasurer.utils;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.List;


public class LatencyFileWriter {
    private File outputFile;

    public LatencyFileWriter(String remoteClusterName) {
        this.outputFile = new File(remoteClusterName);
    }

    public void writeDataIntoFile(List<Long> latencies) {
        if (!latencies.isEmpty()) {
            try {
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileWriter writer = new FileWriter(outputFile, true);
                writer.write(System.nanoTime() + " " + calculateAverage(latencies) + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private double calculateAverage(List<Long> latencies) {
        long averageLatency = 0;
        for (Long latency : latencies) {
            averageLatency += latency;
        }
        averageLatency = averageLatency / latencies.size();
        return ((double) averageLatency / 1000000000);
    }

}
