package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.InputStreamWithNot;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class SpritePackageInputStream {
    private final ByteOffsetInputStream generalInputStream;
    private final SpriteChecksumBuilder spriteChecksumBuilder;
    private int location = 0;

    public byte[] readNBytes(int n) throws IOException {
        byte[] bytes = generalInputStream.readNBytes(n);
        spriteChecksumBuilder.addBytes(bytes, location);
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

    public ArrayList<Integer> getSpriteChecksums() {
        return spriteChecksumBuilder.getChecksums();
    }
}
