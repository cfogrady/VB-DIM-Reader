package com.github.cfogrady.vb.dim.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InMemoryOutputStream implements ByteOffsetOutputStream {
    private int location;
    private ByteArrayOutputStream byteArrayOutputStream;

    public byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }

    public InMemoryOutputStream(int startingLocation) {
        this.location = startingLocation;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public void writeZerosUntilOffset(int offset) throws IOException {
        if(offset > location) {
            byte[] bytes = new byte[offset - location]; //
            this.writeBytes(bytes);
        }
    }

    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        byteArrayOutputStream.write(bytes);
        location += bytes.length;
    }
}
