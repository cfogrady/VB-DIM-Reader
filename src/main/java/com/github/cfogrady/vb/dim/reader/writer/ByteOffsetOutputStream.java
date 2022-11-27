package com.github.cfogrady.vb.dim.reader.writer;

import java.io.IOException;

public interface ByteOffsetOutputStream {

    void writeZerosUntilOffset(int offset) throws IOException;

    void writeBytes(byte[] bytes) throws IOException;
}
