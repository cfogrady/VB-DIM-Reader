package com.github.cfogrady.vb.dim.util;

import java.io.IOException;
import java.io.InputStream;

public class RelativeByteOffsetInputStream implements ByteOffsetInputStream {
    private final InputStream inputStream;
    private final ByteOffsetInputStream byteOffsetInputStream;
    private final boolean wrapsAnotherByteOffsetInputStream;
    private int location = 0;

    public RelativeByteOffsetInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.byteOffsetInputStream = null;
        this.wrapsAnotherByteOffsetInputStream = false;
    }

    public RelativeByteOffsetInputStream(ByteOffsetInputStream byteOffsetInputStream) {
        this.byteOffsetInputStream = byteOffsetInputStream;
        this.inputStream = null;
        this.wrapsAnotherByteOffsetInputStream = true;
    }


    @Override
    public byte[] readNBytes(int numberOfBytes) throws IOException {
        location += numberOfBytes;
        if(wrapsAnotherByteOffsetInputStream) {
            return byteOffsetInputStream.readNBytes(numberOfBytes);
        } else {
            return readNBytes(inputStream, numberOfBytes);
        }
    }

    @Override
    public byte[] readToOffset(int offset) throws IOException {
        if(offset <= location) {
            return new byte[0];
        }
        return this.readNBytes(offset - location);
    }
}
