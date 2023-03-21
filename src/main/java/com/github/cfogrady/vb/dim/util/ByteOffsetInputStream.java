package com.github.cfogrady.vb.dim.util;

import java.io.IOException;
import java.io.InputStream;

public interface ByteOffsetInputStream {
    byte[] readNBytes(int numberOfBytes) throws IOException;

    byte[] readToOffset(int offset) throws IOException;

    default byte[] readNBytes(InputStream inputStream, int n) throws IOException {
        byte[] readBytes = new byte[n];
        inputStream.read(readBytes);
        return readBytes;
    }
}
