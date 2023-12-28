package com.github.cfogrady.vb.dim.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

public interface ByteOffsetInputStream {
    byte[] readNBytes(int numberOfBytes) throws IOException;

    byte[] readToOffset(int offset) throws IOException;

    /**
     * Reads n bytes from the input stream. Blocks until we either reach the end of the stream
     * or have read n bytes
     * @param inputStream Input stream from which to read bytes
     * @param n Number of bytes to read
     * @return byte[] read from the input stream.
     * @throws IOException Thrown if the stream is interrupted or if we reach the end of the stream while still expecting bytes
     */
    default byte[] readNBytes(InputStream inputStream, int n) throws IOException {
        byte[] readBytes = new byte[n];
        int remainingBytesToBeRead = n;
        do {
            byte[] buffer = new byte[remainingBytesToBeRead];
            int actualRead = inputStream.read(buffer);
            if(actualRead == -1) {
                throw new IOException("End of stream reached after reading " + (n-remainingBytesToBeRead) + " bytes");
            } else {
                System.arraycopy(buffer, 0, readBytes, n - remainingBytesToBeRead, actualRead);
                remainingBytesToBeRead -= actualRead;
            }
        } while (remainingBytesToBeRead > 0);
        return readBytes;
    }
}
