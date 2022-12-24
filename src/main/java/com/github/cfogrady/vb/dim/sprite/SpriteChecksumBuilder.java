package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RawChecksumBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class SpriteChecksumBuilder {
    private final ArrayList<RawChecksumBuilder> checksumBuilders;
    private final SpriteChecksumAreasCalculator spriteChecksumAreasCalculator;

    public SpriteChecksumBuilder() {
        this(SpriteChecksumAreasCalculator.buildForDIM());
    }

    public SpriteChecksumBuilder(SpriteChecksumAreasCalculator spriteChecksumAreasCalculator) {
        checksumBuilders = new ArrayList<>();
        for(int i = 0; i < spriteChecksumAreasCalculator.getNumberOfChunks(); i++) {
            checksumBuilders.add(new RawChecksumBuilder());
        }
        this.spriteChecksumAreasCalculator = spriteChecksumAreasCalculator;
    }

    public void addBytes(byte[] bytes, int currentRelativeLocation) {
        int[] unsigned16BitValues = ByteUtils.getUnsigned16Bit(bytes);
        for(int i = 0; i < unsigned16BitValues.length; i++) {
            int wordLocation = currentRelativeLocation + i*2;
            if(spriteChecksumAreasCalculator.isPartOfChecksum(wordLocation)) {
                add16BitInt(unsigned16BitValues[i], wordLocation);
            }
        }
    }



    private void add16BitInt(int value, int relativeLocation) {
        int chunk = spriteChecksumAreasCalculator.calculateWhichChunk(relativeLocation);
        checksumBuilders.get(chunk).add16BitInt(value);
    }

    public ArrayList<Integer> getChecksums() {
        ArrayList<Integer> currentChecksums = new ArrayList<>();
        for(RawChecksumBuilder rawChecksumBuilder : checksumBuilders) {
            currentChecksums.add(rawChecksumBuilder.getChecksum());
        }
        return currentChecksums;
    }
}
