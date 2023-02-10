package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.RawChecksumBuilder;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BemSpriteReaderTest {
    private final Random random = new Random();
    private BemSpriteReader bemSpriteReader;
    private BemSpriteWriter bemSpriteWriter;

    @BeforeEach
    void setup() {
        this.bemSpriteReader = new BemSpriteReader();
        SpriteChecksumAreasCalculator spriteChecksumAreasCalculator = SpriteChecksumAreasCalculator.buildForBEM();
        SpriteChecksumHacker checksumHacker = new SpriteChecksumHacker(spriteChecksumAreasCalculator, SpriteWriter.PIXEL_POINTER_TABLE_START);
        UnorderedSpriteChecksumHacker unorderChecksumHacker = new UnorderedSpriteChecksumHacker(spriteChecksumAreasCalculator, SpriteWriter.PIXEL_POINTER_TABLE_START, new RawChecksumBuilder());
        SpriteWriter spriteWriter = new SpriteWriter(checksumHacker, unorderChecksumHacker);
        this.bemSpriteWriter = new BemSpriteWriter(spriteWriter);
    }

    @Test
    void testReadSpriteDimensions() {
        int numberOfSprites = 371;
        List<SpriteData.SpriteDimensions> expectedSpriteDimensions = createSpriteDimensions(numberOfSprites);
        byte[] table = getTableInBytes(expectedSpriteDimensions);
        Assertions.assertEquals(numberOfSprites*2*2 + 4, table.length);
        List<SpriteData.SpriteDimensions> readDimensions = bemSpriteReader.readSpriteDimensions(new RelativeByteOffsetInputStream(new ByteArrayInputStream(table)));
        Assertions.assertEquals(expectedSpriteDimensions.size(), readDimensions.size());
        for(int i = 0; i < readDimensions.size(); i++) {
            SpriteData.SpriteDimensions expected = expectedSpriteDimensions.get(i);
            SpriteData.SpriteDimensions read = readDimensions.get(i);
            Assertions.assertEquals(expected.getWidth(), read.getWidth());
            Assertions.assertEquals(expected.getHeight(), read.getHeight());
        }
    }

    private List<SpriteData.SpriteDimensions> createSpriteDimensions(int number) {
        List<SpriteData.SpriteDimensions> entries = new ArrayList<>(number);
        for(int i = 0; i < number; i++) {
            entries.add(SpriteData.SpriteDimensions.builder()
                    .width(random.nextInt(81) + 1)
                    .height(random.nextInt(161) + 1)
                    .build());
        }
        return entries;
    }

    private byte[] getTableInBytes(List<SpriteData.SpriteDimensions> spriteDimensions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemSpriteWriter.writeSpriteDimensions(spriteDimensions, new RelativeByteOffsetOutputStream(outputStream));
        //extra 4 bytes to represent 0s after the dimensions are finished being read
        outputStream.write(0);
        outputStream.write(0);
        outputStream.write(0);
        outputStream.write(0);
        return outputStream.toByteArray();
    }
}
