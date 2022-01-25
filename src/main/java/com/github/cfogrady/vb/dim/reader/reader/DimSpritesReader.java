package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class DimSpritesReader {
    static SpriteData spriteDataFromBytesAndStream(byte[] spriteDimensionBytes, InputStreamWithNot spriteDataSection) throws IOException {
        int[] spriteDimensions = ByteUtils.getUnsigned16Bit(spriteDimensionBytes);
        String text = new String(spriteDataSection.readNBytes(0x18));
        int finalOffset = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        spriteDataSection.readToOffset(0x100048);
        int numberOfSprites = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        int[] spriteOffsets = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(numberOfSprites*4));
        int oneMore = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        log.info("Bytes between final offset and sprites: {} - {}", oneMore, Integer.toHexString(oneMore));
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
        return SpriteData.builder().sprites(sprites).text(text).build();
    }
}
