package com.googlecode.icegem.serialization.perf.impl;

import java.io.DataOutput;
import java.io.IOException;

/**
 * @author igolovach
 */

public class NullDataOutput implements DataOutput {

    public void write(int b) throws IOException {
        // NOP
    }

    public void write(byte[] b) throws IOException {
        // NOP
    }

    public void write(byte[] b, int off, int len) throws IOException {
        // NOP
    }

    public void writeBoolean(boolean v) throws IOException {
        // NOP
    }

    public void writeByte(int v) throws IOException {
        // NOP
    }

    public void writeShort(int v) throws IOException {
        // NOP
    }

    public void writeChar(int v) throws IOException {
        // NOP
    }

    public void writeInt(int v) throws IOException {
        // NOP
    }

    public void writeLong(long v) throws IOException {
        // NOP
    }

    public void writeFloat(float v) throws IOException {
        // NOP
    }

    public void writeDouble(double v) throws IOException {
        // NOP
    }

    public void writeBytes(String s) throws IOException {
        // NOP
    }

    public void writeChars(String s) throws IOException {
        // NOP
    }

    public void writeUTF(String s) throws IOException {
        // NOP
    }
}
