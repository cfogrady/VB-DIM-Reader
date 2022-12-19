package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.InputStreamWithNot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.cfogrady.vb.dim.sprite.SpriteWriter.NUMBER_OF_SPRITES_LOCATION;

@Slf4j
public class DimSpritesReader {
    public static SpriteData spriteDataFromBytesAndStream(byte[] spriteDimensionBytes, InputStreamWithNot generalInputStream) throws IOException {
        SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder();
        SpritePackageInputStream spriteDataSection = new SpritePackageInputStream(generalInputStream, spriteChecksumBuilder);
        int[] spriteDimensions = ByteUtils.getUnsigned16Bit(spriteDimensionBytes);
        String text = new String(spriteDataSection.readNBytes(0x18));
        int finalOffset = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        spriteDataSection.readToOffset(NUMBER_OF_SPRITES_LOCATION);
        int numberOfSprites = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        int[] pointers = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes((numberOfSprites+1)*4));
        int endSignalLocation = pointers[pointers.length-1];
        if(finalOffset != endSignalLocation) {
            log.warn("End signal pointer {} at 0x100018 doesn't match the pointer at the end of the pointer table {}.", finalOffset, endSignalLocation);
        }
        List<SpriteData.Sprite> sprites = new ArrayList<>(numberOfSprites);
        int currentOffset = pointers[0];
        for(int i = 0; i < numberOfSprites; i++)
        {
            spriteDataSection.readToOffset(currentOffset);
            if(pointers[i+1] - currentOffset <= 0) {
                throw new IllegalStateException("Unable to handle case where sprites are not in order. If this is an official DIM please raise an issue at https://github.com/cfogrady/VB-DIM-Reader/issues");
            }
            int width = spriteDimensions[i*2];
            int height = spriteDimensions[i*2 + 1];
            int expectedSize = width * height * 2;
            SpriteData.Sprite sprite = SpriteData.Sprite.builder()
                    .width(width)
                    .height(height)
                    .pixelData(spriteDataSection.readNBytes(expectedSize))
                    .build();
            if(sprite.getPixelData().length > pointers[i+1] - currentOffset) {
                throw new IllegalStateException("Expected sprite size " + sprite.getPixelData().length + " is too big to fit between current pointer " + currentOffset + " and next sprite's pointer " + pointers[i+1]);
            }
            currentOffset = pointers[i+1];
            sprites.add(sprite);
        }
        return SpriteData.builder().sprites(sprites).text(text).spriteChecksums(spriteDataSection.getSpriteChecksums()).build();
    }
}
