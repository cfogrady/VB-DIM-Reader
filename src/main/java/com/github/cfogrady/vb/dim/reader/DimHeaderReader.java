package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.DimHeader;
import com.github.cfogrady.vb.dim.reader.content.DimStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DimHeaderReader {
    static DimHeader dimHeaderFromBytes(byte[] bytes) {
        if(bytes.length < 0x102F) {
            throw new IllegalArgumentException("Not enough bytes for header!");
        }
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        return DimHeader.builder()
                .text(new String(Arrays.copyOfRange(bytes, 0x10, 0x2F)))
                .dimId(values[0x32/2])
                .productionYear(values[0x36/2])
                .productionMonth(values[0x38/2])
                .productionDay(values[0x3a/2])
                .revisionNumber(values[0x3c/2])
                .headerSignature(Arrays.copyOfRange(bytes, 0x40, 0x5f))
                .has0x8fSet(bytes[0x8f] != 0)
                .spriteSignature(Arrays.copyOfRange(bytes, 0x1010, 0x102f))
                .build();
    }
}
