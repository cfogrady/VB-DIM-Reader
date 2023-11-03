package com.github.cfogrady.vb.dim.header;

import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Random;

public class BemHeaderReaderTest {
    private static Random random = new Random();
    private BemHeaderWriter bemHeaderWriter;
    private BemHeaderReader bemHeaderReader;

    @BeforeEach
    private void setup() {
        bemHeaderWriter = new BemHeaderWriter();
        bemHeaderReader = new BemHeaderReader();
    }

    @Test
    void testAllBemDataIsBroughtBack() {
        BemHeader sampleHeader = createSampleHeader();
        byte[] headerBytes = getBytesForHeader(sampleHeader);
        BemHeader readHeader = bemHeaderReader.readBemHeaderFromHeaderBytes(headerBytes);
        Assertions.assertArrayEquals(sampleHeader.getHeaderSignature(), readHeader.getHeaderSignature());
        Assertions.assertEquals(sampleHeader.getDimId(), readHeader.getDimId());
        Assertions.assertEquals(sampleHeader.getText(), readHeader.getText());
        Assertions.assertEquals(sampleHeader.getProductionDay(), readHeader.getProductionDay());
        Assertions.assertEquals(sampleHeader.getProductionMonth(), readHeader.getProductionMonth());
        Assertions.assertEquals(sampleHeader.getProductionYear(), readHeader.getProductionYear());
        Assertions.assertEquals(sampleHeader.getRevisionNumber(), readHeader.getRevisionNumber());
        Assertions.assertArrayEquals(sampleHeader.getSpriteSignature(), readHeader.getSpriteSignature());
        Assertions.assertArrayEquals(sampleHeader.getBemFlags(), readHeader.getBemFlags());
    }

    @Test
    void test2ByteDimIdCheckWrittenWithLowerByteOnly() {
        BemHeader sampleHeader = createSampleHeader().toBuilder().dimId(297).build();
        byte[] headerBytes = getBytesForHeader(sampleHeader);
        byte[] dimIdBytes = Arrays.copyOfRange(headerBytes, 0x32, 0x34);
        byte[] dimIdCheckBytes = Arrays.copyOfRange(headerBytes, 0x34, 0x36);
        Assertions.assertEquals(sampleHeader.getDimId(), ByteUtils.getUnsigned16Bit(dimIdBytes)[0]);
        Assertions.assertEquals(sampleHeader.getDimId()&0xFF, ByteUtils.getUnsigned16Bit(dimIdCheckBytes)[0]);
    }

    private byte[] createRandomBytes(int size) {
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return bytes;
    }

    private byte[] getBytesForHeader(BemHeader header) {

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            bemHeaderWriter.writeHeader(header, new RelativeByteOffsetOutputStream(outputStream));
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BemHeader createSampleHeader() {
        String thirtyTwoCharacterString = "cfogrady_0_0_DIM_READER_0_0_0_0_";
        return BemHeader.builder()
                .dimId(42)
                .headerSignature(createRandomBytes(0x60 - 0x40))
                .has0x8fSet(false)
                .productionDay(19)
                .productionMonth(12)
                .productionYear(22)
                .revisionNumber(0)
                .text(thirtyTwoCharacterString)
                .bemFlags(createRandomBytes(0x1010 - 0x1000))
                .spriteSignature(createRandomBytes(0x1030-0x1010))
                .build();
    }
}
