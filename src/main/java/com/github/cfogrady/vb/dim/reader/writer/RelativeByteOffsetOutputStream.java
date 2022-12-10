package com.github.cfogrady.vb.dim.reader.writer;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Wraps a ByteOffsetOutputStream with a new relative location of 0
 */
@RequiredArgsConstructor
public class RelativeByteOffsetOutputStream implements ByteOffsetOutputStream {
    private final ByteOffsetOutputStream byteOffsetOutputStream;
    private int location = 0;

    @Override
    public void writeZerosUntilOffset(int offset) throws IOException {
        if(offset > location) {
            byte[] bytes = new byte[offset - location]; //
            this.writeBytes(bytes);
        }
    }

    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        byteOffsetOutputStream.writeBytes(bytes);
    }
}
