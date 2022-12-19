package com.github.cfogrady.vb.dim.reader;

public class RawChecksumBuilder {
    private int currentSum = 0;

    public void addBytes(byte[] bytes) {
        int[] unsigned16BitValues = ByteUtils.getUnsigned16Bit(bytes);
        for(int i = 0; i < unsigned16BitValues.length; i++) {
            add16BitInt(unsigned16BitValues[i]);
        }
    }

    public void add16BitInt(int value) {
        currentSum = (currentSum + value) & 0xFFFF;
    }

    public int getChecksum() {
        return currentSum;
    }

    public void reset() {
        currentSum = 0;
    }
}
