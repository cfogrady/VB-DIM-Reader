package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetInputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.cfogrady.vb.dim.sprite.SpriteWriter.NUMBER_OF_SPRITES_LOCATION;

@Slf4j
@RequiredArgsConstructor
public class UnorderedSpriteReader {
    private final SpriteChecksumAreasCalculator spriteChecksumAreasCalculator;


    public SpriteData spriteDataFromDimensionsAndStream(List<SpriteData.SpriteDimensions> spriteDimensions, ByteOffsetInputStream generalInputStream) throws IOException {
        SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder(spriteChecksumAreasCalculator);
        SpritePackageInputStream spriteDataSection = new SpritePackageInputStream(generalInputStream, spriteChecksumBuilder);
        String text = new String(spriteDataSection.readNBytes(0x18));
        int finalOffset = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        spriteDataSection.readToOffset(NUMBER_OF_SPRITES_LOCATION);
        int numberOfSprites = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        int[] pointers = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes((numberOfSprites)*4));
        int endSignalLocation = ByteUtils.getIntsFromBytes(spriteDataSection.readNBytes(4))[0];
        List<PointerWithSpriteIndex> pointersWithSpriteIndices = getPointersWithSpriteIndices(pointers);
        if(finalOffset != endSignalLocation) {
            log.warn("End signal pointer {} at 0x100018 doesn't match the pointer at the end of the pointer table {}.", finalOffset, endSignalLocation);
        }
        pointersWithSpriteIndices.sort(Comparator.comparing(PointerWithSpriteIndex::getPointer));
        List<SpriteWithIndex> spritesWithIndices = new ArrayList<>(numberOfSprites);
        for(PointerWithSpriteIndex pointerWithSpriteIndex : pointersWithSpriteIndices) {
            int offset = pointerWithSpriteIndex.getPointer();
            spriteDataSection.readToOffset(offset);
            SpriteData.SpriteDimensions dimensions = spriteDimensions.get(pointerWithSpriteIndex.getIndex());
            int width = dimensions.getWidth();
            int height = dimensions.getHeight();
            int expectedSize = width * height * 2;
            SpriteData.Sprite sprite = SpriteData.Sprite.builder()
                    .width(width)
                    .height(height)
                    .pixelData(spriteDataSection.readNBytes(expectedSize))
                    .build();
            spritesWithIndices.add(new SpriteWithIndex(sprite, pointerWithSpriteIndex.getIndex()));
        }
        spriteDataSection.readToOffset(endSignalLocation+4);
        spritesWithIndices.sort(Comparator.comparing(SpriteWithIndex::getIndex));
        List<SpriteData.Sprite> sprites = spritesWithIndices.stream().map(SpriteWithIndex::getSprite).collect(Collectors.toList());
        return SpriteData.builder().sprites(sprites).text(text).spriteChecksums(spriteDataSection.getSpriteChecksums()).build();
    }

    @Data
    @RequiredArgsConstructor
    static class PointerWithSpriteIndex {
        private final int index;
        private final int pointer;
    }

    private List<PointerWithSpriteIndex> getPointersWithSpriteIndices(int[] pointers) {
        List<PointerWithSpriteIndex> pointersWithSpriteIndices = new ArrayList<>();
        for(int i = 0; i < pointers.length; i++) {
            pointersWithSpriteIndices.add(new PointerWithSpriteIndex(i, pointers[i]));
        }
        return pointersWithSpriteIndices;
    }
}
