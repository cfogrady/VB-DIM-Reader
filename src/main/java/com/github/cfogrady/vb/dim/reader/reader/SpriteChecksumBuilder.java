package com.github.cfogrady.vb.dim.reader.reader;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.RawChecksumBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class SpriteChecksumBuilder {
    public static final int NUMBER_OF_CHUNKS = 28;
    public static final int CHUNK_SIZE = 0x10000;
    public static final int RELATIVE_CHECKSUM_START_LOCATION = 0x2000; //relative to start of sprite package
    public static final int CHUNK_CHECKSUM_PORTION = 0x1000;
    static final int WORD_SPACE = (int) Math.pow(2, 16);

    private final ArrayList<RawChecksumBuilder> checksumBuilders;

    public SpriteChecksumBuilder() {
        checksumBuilders = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_CHUNKS; i++) {
            checksumBuilders.add(new RawChecksumBuilder());
        }
    }

    public void addBytes(byte[] bytes, int currentRelativeLocation) {
        int[] unsigned16BitValues = ByteUtils.getUnsigned16Bit(bytes);
        for(int i = 0; i < unsigned16BitValues.length; i++) {
            int wordLocation = currentRelativeLocation + i*2;
            if(isPartOfChecksum(wordLocation)) {
                add16BitInt(unsigned16BitValues[i], wordLocation);
            }
        }
    }

    private static boolean isAfterChecksumStart(int relativeLocation) {
        return relativeLocation > RELATIVE_CHECKSUM_START_LOCATION;
    }

    public static int calculateWhichChunk(int relativeLocation) {
        int relativeLoc = relativeLocation - RELATIVE_CHECKSUM_START_LOCATION;
        return relativeLoc / CHUNK_SIZE;
    }

    public static int nextChecksumPortion(int relativeLocation) {
        int relativeLoc = relativeLocation - RELATIVE_CHECKSUM_START_LOCATION;
        int currentLocationChunk = relativeLoc / CHUNK_SIZE;
        if(currentLocationChunk >= NUMBER_OF_CHUNKS-1) {
            //there is no max portion if we are already in or past the number of chunks
            log.info("No next location. Returning max value as the next checksum start.");
            return Integer.MAX_VALUE;
        }
        int startsAt = ((currentLocationChunk + 1) * CHUNK_SIZE) + RELATIVE_CHECKSUM_START_LOCATION;
        log.info("Next Checksum starts at 0x{}. Next index: {}", Integer.toHexString(startsAt), currentLocationChunk + 1);
        return startsAt;
    }

    public static int nextChecksumEnd(int relativeLocation) {
        int locationRelativeToChecksumStart = relativeLocation - RELATIVE_CHECKSUM_START_LOCATION;
        int locInChunk = locationRelativeToChecksumStart % CHUNK_SIZE;
        int currentLocationChunk = locationRelativeToChecksumStart / CHUNK_SIZE;
        if(locInChunk >= CHUNK_CHECKSUM_PORTION) {
            currentLocationChunk++;
        }
        return currentLocationChunk * CHUNK_SIZE + CHUNK_CHECKSUM_PORTION + RELATIVE_CHECKSUM_START_LOCATION;
    }

    public static boolean isPartOfChecksum(int relativeLocation) {
        int locationRelativeToChecksumStart = relativeLocation - RELATIVE_CHECKSUM_START_LOCATION;
        int locInChunk = locationRelativeToChecksumStart % CHUNK_SIZE;
        if(isAfterChecksumStart(relativeLocation) &&
                locInChunk < CHUNK_CHECKSUM_PORTION &&
                locationRelativeToChecksumStart / CHUNK_SIZE < NUMBER_OF_CHUNKS) {
            return true;
        }
        return false;
    }

    public static int calculateChecksumOffset(int expected, int current) {
        if(expected == current) {
            return 0;
        } else if(expected > current) {
            return expected - current;
        } else {
            expected += WORD_SPACE;
            return expected - current;
        }
    }

    private void add16BitInt(int value, int relativeLocation) {
        int chunk = calculateWhichChunk(relativeLocation);
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
