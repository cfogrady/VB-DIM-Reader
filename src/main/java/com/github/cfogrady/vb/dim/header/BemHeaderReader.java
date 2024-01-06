package com.github.cfogrady.vb.dim.header;

import java.util.Arrays;

public class BemHeaderReader {
    public BemHeader readBemHeaderFromHeaderBytes(byte[] headerBytes) {
        BemHeader.BemHeaderBuilder<?, ?> builder = BemHeader.builder();
        DimHeaderReader.dimHeaderForBytes(headerBytes, builder);
        builder.bemFlags(Arrays.copyOfRange(headerBytes, 0x1000, 0x1010));
        return builder.build();
    }
}
