package com.googlecode.icegem.cacheutils.latencymeasurer.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;


public class LatencyValueSequence {
    private int maxCount;
    private List<Long> sequence;
    private LatencyFileWriter writer;
    private ExecutorService executor;

    public LatencyValueSequence(int sequenceLength, String remoteClusterName) {
        this.maxCount = sequenceLength;
        this.sequence = new ArrayList<Long>();
        this.writer = new LatencyFileWriter(remoteClusterName);
       // this.executor = Executors.newSingleThreadExecutor();
    }

    public void addItem(Long newValue, String key) {
        sequence.add(newValue);
        key = key.substring(key.lastIndexOf("_") + 1, key.length());
        int iKey = Integer.parseInt(key);
        if ((iKey + 1) == maxCount) {
            writer.writeDataIntoFile(sequence);
            //TODO: figure out why this doesn't work
            //executor.execute(new Runnable() {
               // public void run() {
              //      writer.writeDataIntoFile(sequence);
             //   }
           // });
            sequence = new ArrayList<Long>();
        }
    }

}
