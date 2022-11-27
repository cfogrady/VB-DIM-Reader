package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import com.github.cfogrady.vb.dim.reader.reader.SpriteChecksumBuilder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

@RequiredArgsConstructor
class OutputStreamWithNot implements ByteOffsetOutputStream {
    private final OutputStream outputStream;
    private final ChecksumBuilder checksumBuilder;
    private int location = 0;
    public static OutputStreamWithNot wrap(OutputStream outputStream, ChecksumBuilder checksumBuilder) {
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

    public int getChecksum() {
        return checksumBuilder.getCheckSum();
    }

}
