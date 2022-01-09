package com.github.cfogrady.vb.dim.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChecksumBuilder {
    private int currentSum = 0;

    public void addBytes(byte[] bytes) {
        add16BitInts(ByteUtils.getUnsigned16Bit(bytes));
    }

    public void add16BitInts(int[] ints) {
        for(int i = 0; i < ints.length; i++) {
            currentSum = (currentSum + ints[i]) & 0xFFFF;
        }
    }

    public int getCheckSum() {
        return ByteBuffer
                .allocate(2).order(ByteOrder.BIG_ENDIAN)
                .putInt(currentSum)
                .order(ByteOrder.LITTLE_ENDIAN).asIntBuffer()
                .get();
    }
}
