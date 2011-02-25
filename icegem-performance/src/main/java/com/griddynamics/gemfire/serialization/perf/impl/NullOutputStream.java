package com.griddynamics.gemfire.serialization.perf.impl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author igolovach
 */

public class NullOutputStream extends OutputStream {
    @Override
    public void write(int b) throws IOException {
        // NOP
    }

    public NullOutputStream() {
        // NOP
    }

    @Override
    public void write(byte[] b) throws IOException {
        // NOP
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // NOP
    }

    @Override
    public void flush() throws IOException {
        // NOP
    }

    @Override
    public void close() throws IOException {
        // NOP
    }
}
