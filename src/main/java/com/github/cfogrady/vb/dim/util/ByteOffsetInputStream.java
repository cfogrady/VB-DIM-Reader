package com.github.cfogrady.vb.dim.util;

import java.io.IOException;

public interface ByteOffsetInputStream {
    byte[] readNBytes(int numberOfBytes) throws IOException;

    byte[] readToOffset(int offset) throws IOException;
}
