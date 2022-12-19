package com.github.cfogrady.vb.dim.util;

import java.io.IOException;

public interface ByteOffsetOutputStream {

    void writeZerosUntilOffset(int offset) throws IOException;

    void writeBytes(byte[] bytes) throws IOException;

    default void write16BitInt(int value) throws IOException {
        byte[] word = ByteUtils.convert16BitIntToBytes(value);
        writeBytes(word);
    }

    default void writeInt(int value) throws IOException {
        byte[] word = ByteUtils.convert32BitIntToBytes(value);
        writeBytes(word);
    }
}
