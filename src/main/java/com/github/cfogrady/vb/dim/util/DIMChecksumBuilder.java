package com.github.cfogrady.vb.dim.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class DIMChecksumBuilder {
    private RawChecksumBuilder rawChecksumBuilder = new RawChecksumBuilder();
    // These must be in order
    private Set<Integer> skippedLocations = Set.of(0x10000, 0x10002, 0x10004, 0x10006, 0x3ffffe);

    public void addBytes(byte[] bytes, int currentLocation) {
        int[] unsigned16BitValues = ByteUtils.getUnsigned16Bit(bytes);
        for(int i = 0; i < unsigned16BitValues.length; i++) {
            if(!skippedLocations.contains(i*2 + currentLocation)) {
                rawChecksumBuilder.add16BitInt(unsigned16BitValues[i]);
            } else {
                log.info("Skipping bytes at {}", Integer.toHexString(currentLocation + i*2));
            }
        }
    }

    public int getCheckSum() {
        return rawChecksumBuilder.getChecksum();
    }
}
