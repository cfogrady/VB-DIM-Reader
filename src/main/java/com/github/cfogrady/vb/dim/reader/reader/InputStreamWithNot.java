package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.ChecksumBuilder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
class InputStreamWithNot {
    private final InputStream inputStream;
    private final ChecksumBuilder checksumBuilder;
    private int location = 0;
    public static InputStreamWithNot wrap(InputStream inputStream, ChecksumBuilder checksumBuilder) {
        return new InputStreamWithNot(inputStream, checksumBuilder);
    }

    public byte[] readNBytes(int n) throws IOException {
        byte[] bytes = inputStream.readNBytes(n);
        bytes = ByteUtils.applyNotOperation(bytes);
        checksumBuilder.addBytes(bytes, location);
        location += n;
        return bytes;
    }

    /**
     * Reads from the current location, up until the specified offset.
     * The offset is respective to the start of the underlying stream.
     * Returns an empty byte array if the specified offset has already been reached.
     * @param offset
     * @return
     */
    public byte[] readToOffset(int offset) throws IOException {
        if(offset <= location) {
            return new byte[0];
        }
        return this.readNBytes(offset - location);
    }

    public int getChecksum() {
        return checksumBuilder.getCheckSum();
    }

}
