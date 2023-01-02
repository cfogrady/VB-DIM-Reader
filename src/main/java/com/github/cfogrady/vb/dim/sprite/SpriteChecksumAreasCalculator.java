package com.github.cfogrady.vb.dim.sprite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SpriteChecksumAreasCalculator {
    private final int numberOfChunks;
    private final int chunkInterval;
    private final int relativeChecksumStartLocation; //relative to start of sprite package
    private final int checksumChunkSize;
    static final int WORD_SPACE = (int) Math.pow(2, 16);

    public static SpriteChecksumAreasCalculator buildForDIM() {
        int numberOfChunks = 28;
        int chunkInterval = 0x10000;
        int relativeChecksumStartLocation = 0x2000;
        int checksumChunkSize = 0x1000;
        return new SpriteChecksumAreasCalculator(numberOfChunks, chunkInterval, relativeChecksumStartLocation, checksumChunkSize);
    }

    public static SpriteChecksumAreasCalculator buildForBEM() {
        int NUMBER_OF_CHUNKS = 28;
        int CHUNK_SIZE = 0x10000;
        int RELATIVE_CHECKSUM_START_LOCATION = 0x2000;
        int CHUNK_CHECKSUM_PORTION = 0x1000;
        return new SpriteChecksumAreasCalculator(NUMBER_OF_CHUNKS, CHUNK_SIZE, RELATIVE_CHECKSUM_START_LOCATION, CHUNK_CHECKSUM_PORTION);
    }

    private boolean isAfterChecksumStart(int relativeLocation) {
        return relativeLocation >= relativeChecksumStartLocation;
    }

    public int calculateWhichChunk(int relativeLocation) {
        int relativeLoc = relativeLocation - relativeChecksumStartLocation;
        return relativeLoc / chunkInterval;
    }

    public int nextChecksumStart(int relativeLocation) {
        if(beforeStart(relativeLocation)) {
            // before the start of the first
            return relativeChecksumStartLocation;
        } else if(afterEnd(relativeLocation)) {
            // We're past the end of the checksum, there is no next checksumPortion
            return Integer.MAX_VALUE;
        }
        int relativeLoc = relativeLocation - relativeChecksumStartLocation;
        int chunkOfNextStart = (relativeLoc / chunkInterval)+1;
        int startsAt = (chunkOfNextStart * chunkInterval) + relativeChecksumStartLocation;
        return startsAt;
    }

    boolean beforeStart(int relativeLocation) {
        return relativeLocation < relativeChecksumStartLocation;
    }

    boolean afterEnd(int relativeLocation) {
        return relativeLocation > (numberOfChunks-1) * chunkInterval + relativeChecksumStartLocation + checksumChunkSize;
    }

    public int nextChecksumEnd(int relativeLocation) {
        if(beforeStart(relativeLocation)) {
            // before the start of the first
            return relativeChecksumStartLocation + checksumChunkSize;
        } else if(afterEnd(relativeLocation)) {
            // We're past the end of the checksum, there is no next checksumPortion
            return Integer.MAX_VALUE;
        }
        int locationRelativeToChecksumStart = relativeLocation - relativeChecksumStartLocation;
        int locInChunk = locationRelativeToChecksumStart % chunkInterval;
        int currentLocationChunk = locationRelativeToChecksumStart / chunkInterval;
        if(locInChunk >= checksumChunkSize) {
            currentLocationChunk++;
        }
        return currentLocationChunk * chunkInterval + checksumChunkSize + relativeChecksumStartLocation;
    }

    public boolean isPartOfChecksum(int relativeLocation) {
        int locationRelativeToChecksumStart = relativeLocation - relativeChecksumStartLocation;
        int locInChunk = locationRelativeToChecksumStart % chunkInterval;
        if(isAfterChecksumStart(relativeLocation) &&
                locInChunk < checksumChunkSize &&
                locationRelativeToChecksumStart / chunkInterval < numberOfChunks) {
            return true;
        }
        return false;
    }

    public int calculateChecksumOffset(int expected, int current) {
        if(expected == current) {
            return 0;
        } else if(expected > current) {
            return expected - current;
        } else {
            expected += WORD_SPACE;
            return expected - current;
        }
    }
}
