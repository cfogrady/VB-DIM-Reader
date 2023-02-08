package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.InputStreamWithNot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.cfogrady.vb.dim.sprite.SpriteWriter.NUMBER_OF_SPRITES_LOCATION;

@Slf4j
@RequiredArgsConstructor
public class DimSpritesReader {
    private final SpriteChecksumAreasCalculator spriteChecksumAreasCalculator;
    public SpriteData spriteDataFromBytesAndStream(byte[] spriteDimensionBytes, InputStreamWithNot generalInputStream) throws IOException {
        List<SpriteData.SpriteDimensions> spriteDimensions = getSpriteDimensionsFromBytes(spriteDimensionBytes);
        return spriteDataFromDimensionsAndStream(spriteDimensions, generalInputStream);
    }

    public SpriteData spriteDataFromDimensionsAndStream(List<SpriteData.SpriteDimensions> spriteDimensions, ByteOffsetInputStream generalInputStream) throws IOException {
        SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder(spriteChecksumAreasCalculator);
        SpritePackageInputStream spriteDataSection = new SpritePackageInputStream(generalInputStream, spriteChecksumBuilder);
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
            SpriteData.SpriteDimensions dimensions = spriteDimensions.get(i);
            int width = dimensions.getWidth();
            int height = dimensions.getHeight();
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

    private List<SpriteData.SpriteDimensions> getSpriteDimensionsFromBytes(byte[] dimensionBytes) {
        int[] spriteDimensionValues = ByteUtils.getUnsigned16Bit(dimensionBytes);
        List<SpriteData.SpriteDimensions> spriteDimensions = new ArrayList<>(spriteDimensionValues.length/2);
        for(int i = 0; i < spriteDimensionValues.length; i+=2) {
            spriteDimensions.add(SpriteData.SpriteDimensions.builder()
                            .width(spriteDimensionValues[i])
                            .height(spriteDimensionValues[i+1])
                    .build());
        }
        return spriteDimensions;
    }
}
