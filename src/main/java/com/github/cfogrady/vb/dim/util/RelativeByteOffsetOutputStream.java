package com.github.cfogrady.vb.dim.util;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a ByteOffsetOutputStream with a new relative location of 0
 */
public class RelativeByteOffsetOutputStream implements ByteOffsetOutputStream {
    private final OutputStream outputStream;
    private final boolean wrapsAnotherByteOffsetOutputStream;
    private final ByteOffsetOutputStream byteOffsetOutputStream;
    private int location = 0;

    public RelativeByteOffsetOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        wrapsAnotherByteOffsetOutputStream = false;
        byteOffsetOutputStream = null;
    }

    public RelativeByteOffsetOutputStream(ByteOffsetOutputStream byteOffsetOutputStream) {
        this.byteOffsetOutputStream = byteOffsetOutputStream;
        this.outputStream = null;
        this.wrapsAnotherByteOffsetOutputStream = true;
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
        location += bytes.length;
        if(wrapsAnotherByteOffsetOutputStream) {
            byteOffsetOutputStream.writeBytes(bytes);
        } else {
            outputStream.write(bytes);
        }
    }
}
