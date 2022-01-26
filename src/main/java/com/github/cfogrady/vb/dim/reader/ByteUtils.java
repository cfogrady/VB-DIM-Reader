package com.github.cfogrady.vb.dim.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import at.favre.lib.bytes.Bytes;

public class ByteUtils {
    public static int[] getUnsigned16Bit(byte[] bytes) {
        if(bytes.length % 2 != 0) {
            throw new IllegalArgumentException("Number of bytes must be multiple of 2 to convert into 16-bit words");
        }
        char[] unsignedValues = new char[bytes.length/2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer().get(unsignedValues);
        int[] values = new int[unsignedValues.length];
        for(int i = 0; i < unsignedValues.length; i++) {
            values[i] = unsignedValues[i];
        }
        return values;
    }

    public static int[] getIntsFromBytes(byte[] bytes) {
        if(bytes.length % 4 != 0) {
            throw new IllegalArgumentException("Number of bytes must be multiple of 4 to convert into 32-bit words");
        }
        int[] values = new int[bytes.length/4];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(values);
        return values;
    }

    public static byte[] convert16BitIntToBytes(int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asCharBuffer().append((char) value);
        return byteBuffer.array();
    }

    public static byte[] convert32BitIntToBytes(int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(value);
        return byteBuffer.array();
    }

    public static byte[] applyNotOperation(byte[] bytes) {
        Bytes wrappedBytes = Bytes.wrap(bytes);
        wrappedBytes = wrappedBytes.not();
        return wrappedBytes.array();
    }

    public static boolean onlyZerosOrMaxValuesInRange(int[] values, int start, int length) {
        //Some cards mark rows of the table as invalid by using max 16-bit int instead of zeros...
        for(int i = start; i < start+length; i++) {
            if(values[i] != 0 && values[i] != 65535) {
                return false;
            }
        }
        return true;
    }
}
