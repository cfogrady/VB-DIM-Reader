package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class UnorderedSpriteChecksumHacker {
    private static final SpriteData.Sprite TERMINATION_SPRITE = SpriteData.Sprite.builder()
            .height(1)
            .width(2)
            .pixelData(ByteUtils.convert32BitIntToBytes(SpriteWriter.TERMINATION_BYTES))
            .build();

    private final SpriteChecksumAreasCalculator spriteChecksumAreasCalculator;
    private final int pixelPointerTableStart;
    private final RawChecksumBuilder rawChecksumBuilder;

    public void writeSpritesUnorderedToCorrectChecksums(SpriteData spriteData, ByteOffsetOutputStream mainOutputStream) throws IOException {
        RelativeByteOffsetOutputStream spritePackageOutputStream = new RelativeByteOffsetOutputStream(mainOutputStream);
        PixelDataWithPointers pixelDataWithPointers = createPixelDataAndPointersSection(spriteData);
        writeMetaToOutput(spriteData, pixelDataWithPointers.getPointerTable(), spritePackageOutputStream);
        writePointerTableToOutput(pixelDataWithPointers.getPointerTable(), spritePackageOutputStream);
        spritePackageOutputStream.writeBytes(pixelDataWithPointers.getPixelData());
    }

    @RequiredArgsConstructor
    @Data
    static class PixelDataWithPointers {
        final byte[] pixelData;
        final int[] pointerTable;
    }

    private PixelDataWithPointers createPixelDataAndPointersSection(SpriteData spriteData) throws IOException {
        SpriteFinder spriteFinder = SpriteFinder.createSpriteFinder(spriteData.getSprites());
        int currentSpot = calculateAddressImmediatelyAfterTable(spriteData);
        int[] pointerTable = new int[spriteData.getSprites().size() + 1];// 1 for end signal
        byte[] checksumArea = new byte[spriteChecksumAreasCalculator.getChecksumChunkSize()];
        InMemoryOutputStream inMemoryOutputStream = new InMemoryOutputStream(currentSpot);
        Integer checksumOffsetLocation = null;
        int checkSumAreaLocation = 0;
        while(!spriteFinder.isEmpty()) {
            if(!spriteChecksumAreasCalculator.isPartOfChecksum(currentSpot)) {
                int nextStart = spriteChecksumAreasCalculator.nextChecksumStart(currentSpot);
                int nextEnd = spriteChecksumAreasCalculator.nextChecksumEnd(currentSpot);
                int size = (nextEnd - 2) - currentSpot;
                SpriteWithIndex spriteWithIndex = spriteFinder.getNextSmallestSize(size);
                if(spriteWithIndex == null) {
                    //nothing fits within the space from here to the next checksum end... skip to the start
                    inMemoryOutputStream.writeZerosUntilOffset(nextStart);
                    currentSpot = nextStart;
                } else {
                    int spriteSize = spriteWithIndex.getSize();
                    if(spriteSize + currentSpot <= nextStart) {
                        // sprite ends before checksum zone
                        inMemoryOutputStream.writeBytes(spriteWithIndex.getPixelData());
                        pointerTable[spriteWithIndex.getIndex()] = currentSpot;
                        currentSpot += spriteWithIndex.getSize();
                    } else {
                        // sprite ends in checksum zone
                        int bytesBeforeChecksumStart = nextStart - currentSpot;
                        // write up to the start of the checksum area
                        inMemoryOutputStream.writeBytes(Arrays.copyOfRange(spriteWithIndex.getPixelData(), 0, bytesBeforeChecksumStart));
                        // write the rest of the sprite in the checksum area buffer for calculating sprite checksum offset
                        checkSumAreaLocation = writeToArray(spriteWithIndex.getPixelData(), bytesBeforeChecksumStart, checksumArea, checkSumAreaLocation);
                        pointerTable[spriteWithIndex.getIndex()] = currentSpot;
                        currentSpot += spriteWithIndex.getSize();
                    }
                }
            } else {
                // we're already in the checksum
                int nextEnd = spriteChecksumAreasCalculator.nextChecksumEnd(currentSpot);
                SpriteWithIndex spriteWithIndex = spriteFinder.getNextLargestSprite();
                if(checksumOffsetLocation == null) {
                    checksumOffsetLocation = checkSumAreaLocation;
                    currentSpot += 2;
                    checkSumAreaLocation += 2;
                }
                int spriteSize = spriteWithIndex.getSize();
                if(currentSpot + spriteSize < nextEnd) {
                    checkSumAreaLocation += writeToArray(spriteWithIndex.getPixelData(), 0, checksumArea, checkSumAreaLocation);
                    pointerTable[spriteWithIndex.getIndex()] = currentSpot;
                    currentSpot += spriteSize;
                } else {
                    int bytesWritten = writeToArray(spriteWithIndex.getPixelData(), 0, checksumArea, checkSumAreaLocation);
                    pointerTable[spriteWithIndex.getIndex()] = currentSpot;
                    int offset = calculateChecksumOffset(currentSpot, checksumArea, spriteData.getSpriteChecksums());
                    writeOffset(offset, checksumOffsetLocation, checksumArea);
                    inMemoryOutputStream.writeBytes(checksumArea);
                    inMemoryOutputStream.writeBytes(Arrays.copyOfRange(spriteWithIndex.getPixelData(), bytesWritten, spriteSize));
                    currentSpot += spriteSize;
                    checksumOffsetLocation = null;
                    checkSumAreaLocation = 0;
                    clearArray(checksumArea);
                }
            }
        }
        if(checkSumAreaLocation != 0) {
            //we're in the middle of a checksum block
            if(checksumOffsetLocation == null) {
                checksumOffsetLocation = currentSpot;
                currentSpot += 2;
            }
            int offset = calculateChecksumOffset(currentSpot, checksumArea, spriteData.getSpriteChecksums());
            writeOffset(offset, checksumOffsetLocation, checksumArea);
            inMemoryOutputStream.writeBytes(checksumArea);
        }
        if (spriteChecksumAreasCalculator.isPartOfChecksum(currentSpot)) {
            currentSpot = spriteChecksumAreasCalculator.nextChecksumEnd(currentSpot);
            inMemoryOutputStream.writeZerosUntilOffset(currentSpot);
        }
        pointerTable[pointerTable.length-1] = currentSpot;
        inMemoryOutputStream.writeBytes(TERMINATION_SPRITE.getPixelData());

        return new PixelDataWithPointers(inMemoryOutputStream.getBytes(), pointerTable);
    }

    private int calculateAddressImmediatelyAfterTable(SpriteData spriteData) {
        //start of table + int pointer per sprite + int pointer to termination bytes
        return pixelPointerTableStart + 4* spriteData.getSprites().size() + 4;
    }

    private void clearArray(byte[] data) {
        Arrays.fill(data, (byte) 0);
    }

    private void writeOffset(int offset, int offsetLocation, byte[] checksumArea) {
        byte[] srcBytes = ByteUtils.convert16BitIntToBytes(offset);
        writeToArray(srcBytes, 0, checksumArea, offsetLocation);
    }

    /**
     * Copy bytes from the src bytes starting with srcStart to the dst bytes starting with dstStart
     * @return The number of bytes written
     */
    private int writeToArray(byte[] src, int srcStart, byte[] dst, int dstStart) {
        int bytesRemainingInSrc = src.length - srcStart;
        int bytesRemainingInDst = dst.length - dstStart;
        int bytesToCopy = Math.min(bytesRemainingInSrc, bytesRemainingInDst);
        if (bytesToCopy >= 0) {
            System.arraycopy(src, srcStart, dst, dstStart, bytesToCopy);
        }
        return bytesToCopy;
    }

    private int calculateChecksumFromBytes(byte[] data) {
        rawChecksumBuilder.reset();
        rawChecksumBuilder.addBytes(data);
        return rawChecksumBuilder.getChecksum();
    }

    private int calculateChecksumOffset(int currentSpot, byte[] checksumArea, List<Integer> originalChecksums) {
        int currentChecksum = calculateChecksumFromBytes(checksumArea);
        int expectedChecksum = spriteChecksumAreasCalculator.getChecksumForLocationFromList(currentSpot, originalChecksums);
        return spriteChecksumAreasCalculator.calculateChecksumOffset(expectedChecksum, currentChecksum);
    }

    private void writeMetaToOutput(SpriteData spriteData, int[] pointerTable, ByteOffsetOutputStream outputStream) throws IOException {
        outputStream.writeBytes(spriteData.getText().getBytes());
        outputStream.writeZerosUntilOffset(SpriteWriter.TERMINATION_BYTES_OF_POINTER_TABLE);
        int terminationBytesPointer = pointerTable[pointerTable.length-1];
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(terminationBytesPointer));
        outputStream.writeZerosUntilOffset(SpriteWriter.TABLE_START);
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(1)); // 0x40
        //pointer to number of sprites
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(SpriteWriter.NUMBER_OF_SPRITES_LOCATION)); // 0x44
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(spriteData.getSprites().size())); // 0x48
    }

    private void writePointerTableToOutput(int[] pointerTable, ByteOffsetOutputStream outputStream) throws IOException {
        for(Integer pointer : pointerTable) {
            outputStream.writeInt(pointer);
        }
    }
}
