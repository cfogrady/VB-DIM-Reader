package com.github.cfogrady.vb.dim.reader;

import com.github.cfogrady.vb.dim.reader.content.SpriteData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DimSpritesReader {
    static SpriteData spriteDataFromBytesAndStream(byte[] spriteDimensionBytes, InputStreamWithNot spriteDataSection) throws IOException {
        int[] spriteDimensions = ByteUtils.getUnsigned16Bit(spriteDimensionBytes);
        spriteDataSection.readNBytes(0x18);
        int finalOffset = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        spriteDataSection.readNBytes(0x48 - 0x1c);
        int numberOfSprites = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        int[] spriteOffsets = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(numberOfSprites*4));
        List<SpriteData.Sprite> sprites = new ArrayList<>(numberOfSprites);
        int currentOffset = spriteOffsets[0];
        for(int i = 0; i < numberOfSprites; i++)
        {
            int width = spriteDimensions[i*2];
            int height = spriteDimensions[i*2 + 1];
            int expectedSize = width * height * 2;
            SpriteData.Sprite sprite = SpriteData.Sprite.builder()
                    .width(width)
                    .height(height)
                    .pixelData(spriteDataSection.readNBytes(expectedSize))
                    .build();
            if(i != numberOfSprites-1) {
                if(sprite.getPixelData().length != spriteOffsets[i+1] - currentOffset) {
                    throw new IllegalStateException("Expected sprite size " + sprite.getPixelData().length + " doesn't match delta from current offset " + currentOffset + " to next offset " + spriteOffsets[i+1]);
                }
                currentOffset = spriteOffsets[i+1];
            }
            sprites.add(sprite);
        }
        return SpriteData.builder().sprites(sprites).build();
    }
}
