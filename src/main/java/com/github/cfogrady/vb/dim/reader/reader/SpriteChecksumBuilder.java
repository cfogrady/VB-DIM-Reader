package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.RawChecksumBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class SpriteChecksumBuilder {
    public static final int NUMBER_OF_CHUNKS = 28;
    public static final int CHUNK_SIZE = 0x10000;
    public static final int CHECKSUM_START_LOCATION = 0x102000;
    public static final int CHUNK_CHECKSUM_PORTION = 0x1000;

    private final ArrayList<RawChecksumBuilder> checksumBuilders;

    public SpriteChecksumBuilder() {
        checksumBuilders = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_CHUNKS; i++) {
            checksumBuilders.add(new RawChecksumBuilder());
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

    private static boolean isAfterChecksumStart(int location) {
        return location > CHECKSUM_START_LOCATION;
    }

    private static int calculateWhichChunk(int location) {
        int relativeLoc = location - CHECKSUM_START_LOCATION;
        return relativeLoc / CHUNK_SIZE;
    }

    public static int nextChecksumPortion(int location) {
        int relativeLoc = location - CHECKSUM_START_LOCATION;
        int currentLocationChunk = relativeLoc / CHUNK_SIZE;
        return ((currentLocationChunk + 1) * CHUNK_SIZE) + CHECKSUM_START_LOCATION;
    }

    public static int nextChecksumEnd(int location) {
        int relativeLoc = location - CHECKSUM_START_LOCATION;
        int locInChunk = relativeLoc % CHUNK_SIZE;
        int currentLocationChunk = relativeLoc / CHUNK_SIZE;
        if(locInChunk >= CHUNK_CHECKSUM_PORTION) {
            currentLocationChunk++;
        }
        return currentLocationChunk * CHUNK_SIZE + CHUNK_CHECKSUM_PORTION + CHECKSUM_START_LOCATION;
    }

    public static boolean isPartOfChecksum(int location) {
        int relativeLoc = location - CHECKSUM_START_LOCATION;
        int locInChunk = relativeLoc % CHUNK_SIZE;
        if(isAfterChecksumStart(location) &&
                locInChunk < CHUNK_CHECKSUM_PORTION &&
                relativeLoc / CHUNK_SIZE < NUMBER_OF_CHUNKS) {
            return true;
        }
        return false;
    }

    public static boolean includesChecksumArea(int location, int size) {
        int lastByte = location + (size-1);
        if(isPartOfChecksum(location)) {
            return true;
        }
        if(isPartOfChecksum(lastByte)) {
            return true;
        }
        if(calculateWhichChunk(location) != calculateWhichChunk(lastByte)) {
            //if we transitioned to a new chunk then we encapsulated an entire area just in this sprite
            return true;
        }
        if(isAfterChecksumStart(location) != isAfterChecksumStart(lastByte)) {
            //checks for a -0 to 0 type situation.
            return true;
        }
        return false;
    }

    private void add16BitInt(int value, int location) {
        int chunk = calculateWhichChunk(location);
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
