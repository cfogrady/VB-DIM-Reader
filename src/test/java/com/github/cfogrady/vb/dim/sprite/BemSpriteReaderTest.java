package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.character.BemCharacterConstants;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
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
        this.bemSpriteWriter = new BemSpriteWriter();
    }

    @Test
    void testReadSpriteDimensions() {
        int numberOfSprites = 371;
        List<SpriteData.SpriteDimensions> expectedSpriteDimensions = createSpriteDimensions(numberOfSprites);
        byte[] table = getTableInBytes(expectedSpriteDimensions);
        Assertions.assertEquals(numberOfSprites*2*2, table.length);
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
                    .width(random.nextInt(80))
                    .height(random.nextInt(160))
                    .build());
        }
        return entries;
    }

    private byte[] getTableInBytes(List<SpriteData.SpriteDimensions> spriteDimensions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bemSpriteWriter.writeSpriteDimensions(spriteDimensions, new RelativeByteOffsetOutputStream(outputStream));
        return outputStream.toByteArray();
    }
}
