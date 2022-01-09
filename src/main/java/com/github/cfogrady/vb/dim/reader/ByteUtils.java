package com.github.cfogrady.vb.dim.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import at.favre.lib.bytes.Bytes;

public class ByteUtils {
    public static int[] getUnsigned16Bit(byte[] bytes) {
        short[] signedValues = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().array();
        int[] values = new int[signedValues.length];
        for(int i = 0; i < signedValues.length; i++) {
            values[i] = signedValues[i] >= 0 ? signedValues[i] : signedValues[i] + 0x10000;
        }
        return values;
    }

    public static int[] getIntsFromBytes(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().array();
    }

    public static byte[] applyNotOperation(byte[] bytes) {
        Bytes wrappedBytes = Bytes.wrap(bytes);
        wrappedBytes = wrappedBytes.not();
        return wrappedBytes.array();
    }

    public static boolean onlyZerosInRange(int[] values, int start, int length) {
        for(int i = start; i < start+length; i++) {
            if(values[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
