package com.github.cfogrady.vb.dim.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteUtilsTest {
    @Test
    public void testApplyNotOperationWorksOnOddNumberOfBytes() {
        byte[] inputBytes = { (byte)0b11111111, (byte)0b11111111, (byte)0b11111111, (byte)0b11111111, (byte)0b11111111, 0b0, 0b100, 0b10, 0b111};
        byte[] outputBytes = ByteUtils.applyNotOperation(inputBytes);
        Assertions.assertEquals(0b0, outputBytes[0]);
        Assertions.assertEquals(0b0, outputBytes[1]);
        Assertions.assertEquals(0b0, outputBytes[2]);
        Assertions.assertEquals(0b0, outputBytes[3]);
        Assertions.assertEquals(0b0, outputBytes[4]);
        Assertions.assertEquals((byte)0b11111111, outputBytes[5]);
        Assertions.assertEquals((byte)0b11111011, outputBytes[6]);
        Assertions.assertEquals((byte)0b11111101, outputBytes[7]);
        Assertions.assertEquals((byte)0b11111000, outputBytes[8]);
    }
}
