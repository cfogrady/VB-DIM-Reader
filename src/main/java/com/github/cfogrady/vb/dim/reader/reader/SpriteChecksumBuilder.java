package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class SpriteChecksumBuilder {
    public static final int NUMBER_OF_CHUNKS = 28;
    public static final int CHUNK_SIZE = 0x10000;
    public static final int CHECKSUM_START_LOCATION = 0x102000;
    public static final int CHUNK_CHECKSUM_PORTION = 0x1000;

    private final ArrayList<Integer> checksums;

    public SpriteChecksumBuilder() {
        checksums = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_CHUNKS; i++) {
            checksums.add(0);
        }
    }

    public void addBytes(byte[] bytes, int currentLocation) {
        int[] unsigned16BitValues = ByteUtils.getUnsigned16Bit(bytes);
        for(int i = 0; i < unsigned16BitValues.length; i++) {
            int wordLocation = currentLocation + i*2;
            if(isPartOfChecksum(wordLocation)) {
                add16BitInt(unsigned16BitValues[i], wordLocation);
            }
        }
    }

    private int calculateWhichChunk(int location) {
        int relativeLoc = location - CHECKSUM_START_LOCATION;
        return relativeLoc / CHUNK_SIZE;
    }

    private boolean isPartOfChecksum(int location) {
        int relativeLoc = location - CHECKSUM_START_LOCATION;
        int locInChunk = relativeLoc % CHUNK_SIZE;
        if(location > CHECKSUM_START_LOCATION &&
                locInChunk < CHUNK_CHECKSUM_PORTION &&
                relativeLoc / CHUNK_SIZE < NUMBER_OF_CHUNKS) {
            return true;
        }
        return false;
    }

    private void add16BitInt(int value, int location) {
        int chunk = calculateWhichChunk(location);
        checksums.set(chunk, (checksums.get(chunk) + value) & 0xFFFF);
    }

    public ArrayList<Integer> getChecksums() {
        return checksums;
    }
}
