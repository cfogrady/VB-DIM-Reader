package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.RawChecksumBuilder;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.SpriteChecksumBuilder;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class SpriteChecksumHacker {

    private final int PIXEL_POINTER_TABLE_START;
    private final int CHECKSUM_SIZE;

    private int potentialSpriteStartLocation;
    private ArrayList<Integer> pointerTable;
    private InMemoryOutputStream inMemoryPixelDataOutputStream;
    private int nextIndexInChecksumArea;
    private RawChecksumBuilder rawChecksumBuilder;
    private byte[] areaToChecksum;
    private SpriteData spriteData;

    private void setupFromSpriteData(SpriteData spriteData) {
        this.spriteData = spriteData;
        SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder();
        potentialSpriteStartLocation = PIXEL_POINTER_TABLE_START + 4*spriteData.getSprites().size(); // start location + count int + pointer int per sprite
        pointerTable = createDefaultPointerTable();
        inMemoryPixelDataOutputStream = new InMemoryOutputStream(spriteChecksumBuilder, potentialSpriteStartLocation);
        nextIndexInChecksumArea = 0;
        rawChecksumBuilder = new RawChecksumBuilder();
        areaToChecksum = new byte[CHECKSUM_SIZE];
    }

    private ArrayList<Integer> createDefaultPointerTable() {
        ArrayList<Integer> pointerTable = new ArrayList<>(spriteData.getSprites().size());
        int pixelStartLocation = PIXEL_POINTER_TABLE_START + 4*pointerTable.size();
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            pointerTable.add(pixelStartLocation);
            pixelStartLocation += sprite.getByteCountAt16BitPerPixel();
        }
        return pointerTable;
    }

    public void writeInterweavedSpriteTableAndSpritesWithChecksumFixes(SpriteData spriteData, OutputStreamWithNot outputStreamWithNot) throws IOException {
        setupFromSpriteData(spriteData);
        buildInterweavedSpriteTableAndSpritesWithChecksumFixes();
        writePointerTableToOutput(outputStreamWithNot);
        outputStreamWithNot.writeBytes(inMemoryPixelDataOutputStream.getBytes());
    }

    private void buildInterweavedSpriteTableAndSpritesWithChecksumFixes() throws IOException {
        // this is kind of like the knapsack problem. I'm taking a greedy approach, but if we really needed to optimize,
        // it might be worth brushing up on the knapsack problem and look at rearranging sprites so smaller sprites
        // went where they fit between checksum areas even if it meant sprites were out of order.
        for(int index = 0 ; index < spriteData.getSprites().size(); index++) {
            SpriteData.Sprite sprite = spriteData.getSprites().get(index);
            if(nextIndexInChecksumArea != 0) {
                // shares checksum area with previous sprite(s).
                if(checksumEndsInSprite(sprite)) {
                    // sprite finishes checksum
                    copyBytes(sprite.getPixelData(), 0, areaToChecksum, nextIndexInChecksumArea, areaToChecksum.length - nextIndexInChecksumArea);
                    rawChecksumBuilder.reset();
                    rawChecksumBuilder.addBytes(areaToChecksum);
                    int checksumForNormalWrite = rawChecksumBuilder.getChecksum();
                    int expectedChecksum = getExpectedChecksumForCurrentBlock();
                    if(checksumForNormalWrite == expectedChecksum) {
                        //checksum matches so no need to move anything, write normally
                        writeSpriteToOutput(index, sprite);
                    } else {
                        // If bad, push checksum up to checksum start+2
                        writeSpriteToOutputWithChecksumOffset(index, sprite, expectedChecksum, potentialSpriteStartLocation, nextIndexInChecksumArea);
                        nextIndexInChecksumArea = 0;
                    }
                } else {
                    // more in the area after this, so just write and keep going
                    int bytesInSprite = sprite.getByteCountAt16BitPerPixel();
                    copyBytes(sprite.getPixelData(), 0, areaToChecksum, nextIndexInChecksumArea, bytesInSprite);
                    nextIndexInChecksumArea += bytesInSprite;
                    writeSpriteToOutput(index, sprite);
                }
            } else if(checksumStartsInSprite(sprite)) {
                int checksumStartLocation = SpriteChecksumBuilder.nextChecksumPortion(potentialSpriteStartLocation);
                int checksumPortionIndex = SpriteChecksumBuilder.calculateWhichChunk(checksumStartLocation);
                int checksumEndLocation = SpriteChecksumBuilder.nextChecksumEnd(checksumStartLocation);
                if(checksumEndLocation <= potentialSpriteStartLocation + sprite.getByteCountAt16BitPerPixel()) {
                    //fully encompassed within this sprite
                    int expectedChecksum = spriteData.getSpriteChecksums().get(checksumPortionIndex);
                    int checksumForNormalWrite = getChunkChecksumForNormalSpriteWrite(checksumStartLocation, sprite);
                    if(checksumForNormalWrite == expectedChecksum) {
                        //checksum matches so no need to move anything, write normally
                        writeSpriteToOutput(index, sprite);
                    } else {
                        // If bad, push checksum up to checksum start+2
                        writeSpriteToOutputWithChecksumOffset(index, sprite, expectedChecksum, checksumStartLocation, 0);
                    }
                } else {
                    // Sprite only covers part of the checksum area
                    int startOfSpriteInChecksum = checksumStartLocation - potentialSpriteStartLocation;
                    int bytesInChecksumArea = sprite.getByteCountAt16BitPerPixel() - startOfSpriteInChecksum;
                    copyBytes(sprite.getPixelData(), startOfSpriteInChecksum, areaToChecksum, 0, bytesInChecksumArea);
                    nextIndexInChecksumArea = bytesInChecksumArea;
                    // We add the sprite here. If the checksum is no good we'll add an offset between this sprite and the next.
                    writeSpriteToOutput(index, sprite);
                }
            } else {
                //we don't start in a checksum, and we don't enter a checksum, so just write normally
                writeSpriteToOutput(index, sprite);
            }
        }
    }

    private void writePointerTableToOutput(OutputStreamWithNot outputStreamWithNot) throws IOException {
        for(Integer pointer : pointerTable) {
            outputStreamWithNot.writeInt(pointer);
        }
    }

    private void writeSpriteToOutput(int index, SpriteData.Sprite sprite) throws IOException {
        pointerTable.set(index, potentialSpriteStartLocation);
        inMemoryPixelDataOutputStream.writeBytes(sprite.getPixelData());
        potentialSpriteStartLocation += sprite.getByteCountAt16BitPerPixel();
    }

    private int getChunkChecksumForNormalSpriteWrite(int checksumStartLocation, SpriteData.Sprite sprite) {
        int startOfSpriteInChecksum = checksumStartLocation - potentialSpriteStartLocation;
        copyBytes(sprite.getPixelData(), startOfSpriteInChecksum, areaToChecksum, 0, areaToChecksum.length);
        rawChecksumBuilder.reset();
        rawChecksumBuilder.addBytes(areaToChecksum);
        return rawChecksumBuilder.getChecksum();
    }

    private int getExpectedChecksumForCurrentBlock() {
        int checksumStartLocation = SpriteChecksumBuilder.nextChecksumPortion(potentialSpriteStartLocation);
        int checksumPortionIndex = SpriteChecksumBuilder.calculateWhichChunk(checksumStartLocation);
        return spriteData.getSpriteChecksums().get(checksumPortionIndex);
    }

    private void writeSpriteToOutputWithChecksumOffset(int index, SpriteData.Sprite sprite, int expectedChecksum, int checksumStartLocation, int areaToChecksumOffset) throws IOException {
        areaToChecksum[areaToChecksumOffset] = 0;
        areaToChecksum[areaToChecksumOffset + 1] = 0;
        copyBytes(sprite.getPixelData(), 0, areaToChecksum, 2, areaToChecksum.length-(2 + areaToChecksumOffset));
        rawChecksumBuilder.reset();
        rawChecksumBuilder.addBytes(areaToChecksum);
        int checksumOffset = SpriteChecksumBuilder.calculateChecksumOffset(expectedChecksum, rawChecksumBuilder.getChecksum());
        inMemoryPixelDataOutputStream.writeZerosUntilOffset(checksumStartLocation);
        inMemoryPixelDataOutputStream.write16BitInt(checksumOffset);
        //assumes that a single sprite will never cross multiple checksum areas.
        inMemoryPixelDataOutputStream.writeBytes(sprite.getPixelData());
        pointerTable.set(index, checksumStartLocation + 2);
        potentialSpriteStartLocation = checksumStartLocation+2 + sprite.getByteCountAt16BitPerPixel();
    }

    private void copyBytes(byte[] src, int srcStart, byte[] dst, int endStart, int length) {
        for(int i = 0; i < length; i++) {
            dst[i + endStart] = src[i + srcStart];
        }
    }

    private boolean checksumStartsInSprite(SpriteData.Sprite sprite) {
        int spriteEndLocation = potentialSpriteStartLocation + sprite.getByteCountAt16BitPerPixel();
        return SpriteChecksumBuilder.nextChecksumPortion(potentialSpriteStartLocation) < spriteEndLocation;

    }

    private boolean checksumEndsInSprite(SpriteData.Sprite sprite) {
        int spriteEndLocation = potentialSpriteStartLocation + sprite.getByteCountAt16BitPerPixel();
        int endChecksumBlock = SpriteChecksumBuilder.nextChecksumEnd(potentialSpriteStartLocation);
        return spriteEndLocation >= endChecksumBlock;
    }

    public static class InMemoryOutputStream implements ByteOffsetOutputStream {
        private SpriteChecksumBuilder spriteChecksumBuilder;
        private int location;
        private ByteArrayOutputStream byteArrayOutputStream;

        public byte[] getBytes() {
            return byteArrayOutputStream.toByteArray();
        }

        public InMemoryOutputStream(SpriteChecksumBuilder spriteChecksumBuilder, int startingLocation) {
            this.location = startingLocation;
            this.spriteChecksumBuilder = spriteChecksumBuilder;
            byteArrayOutputStream = new ByteArrayOutputStream();
        }

        @Override
        public void writeZerosUntilOffset(int offset) throws IOException {
            if(offset > location) {
                byte[] bytes = new byte[offset - location]; //
                this.writeBytes(bytes);
            }
        }

        @Override
        public void writeBytes(byte[] bytes) throws IOException {
            spriteChecksumBuilder.addBytes(bytes, location);
            byteArrayOutputStream.write(bytes);
            location += bytes.length;
        }
    }
}
