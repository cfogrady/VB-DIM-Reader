package com.github.cfogrady.vb.dim.header;

import com.github.cfogrady.vb.dim.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class DimHeaderReader {
    public static DimHeader dimHeaderFromBytes(byte[] bytes) {
        if(bytes.length < 0x102F) {
            throw new IllegalArgumentException("Not enough bytes for header!");
        }
        int[] values = ByteUtils.getUnsigned16Bit(bytes);
        if(values[0x30] != 0) {
            log.error("Reader only handles data DIMs");
            throw new IllegalArgumentException("Reader only handles data DIMs");
        }
        if(values[0x32/2] != values[0x34/2]) {
            log.warn("Presumed DIM Ids {} and {} do not match! Please make an issue with the DIM card in question on https://github.com/cfogrady/DIM-Modifier/issues so I purchase and analyze the card", values[0x32/2], values[0x34/2]);
        }
        return DimHeader.builder()
                .text(new String(Arrays.copyOfRange(bytes, 0x10, 0x30)))
                .dimId(values[0x32/2])
                .productionYear(values[0x36/2])
                .productionMonth(values[0x38/2])
                .productionDay(values[0x3a/2])
                .revisionNumber(values[0x3c/2])
                .headerSignature(Arrays.copyOfRange(bytes, 0x40, 0x60))
                .has0x8fSet(bytes[0x8f] != 0)
                .spriteSignature(Arrays.copyOfRange(bytes, 0x1010, 0x1030))
                .build();
    }
}
