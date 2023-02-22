package com.github.cfogrady.vb.dim.util;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

@RequiredArgsConstructor
public class OutputStreamWithNot implements ByteOffsetOutputStream {
    private final OutputStream outputStream;
    private final DIMChecksumBuilder checksumBuilder;
    private int location = 0;

    public static OutputStreamWithNot wrap(OutputStream outputStream, DIMChecksumBuilder checksumBuilder) {
        return new OutputStreamWithNot(outputStream, checksumBuilder);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        checksumBuilder.addBytes(bytes, location);
        bytes = ByteUtils.applyNotOperation(bytes);
        outputStream.write(bytes);
        location += bytes.length;
    }

    /**
     * Writes zeros from the current location, up until the specified offset.
     * The offset is respective to the start of the underlying stream.
     * Returns an empty byte array if the specified offset has already been reached.
     * @param offset
     * @return
     */
    public void writeZerosUntilOffset(int offset) throws IOException {
        if(offset > location) {
            byte[] bytes = new byte[offset - location]; //
            this.writeBytes(bytes);
        }
    }

    public void write16BitValueUntilOffset(int value, int offset) throws IOException {
        if(offset > location) {
            int words = (offset - location)/2;
            byte[] word = ByteUtils.convert16BitIntToBytes(value);
            for(int i = 0; i < words; i++) {
                this.writeBytes(word);
            }
        }
    }

    public int getChecksum() {
        return checksumBuilder.getCheckSum();
    }

}
