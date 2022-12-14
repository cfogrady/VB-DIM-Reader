package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.RawChecksumBuilder;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.SpriteChecksumBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
public class SpriteChecksumHacker {

    // This is a hack to make the termination bytes easier to fit into the algorithm
    private static final SpriteData.Sprite TERMINATION_SPRITE = SpriteData.Sprite.builder()
            .height(1)
            .width(2)
            .pixelData(ByteUtils.convert32BitIntToBytes(SpriteWriter.TERMINATION_BYTES))
            .build();

    private final int pixelPointerTableStart;
    private final int checksumSize;

    private int potentialSpriteStartLocation;
    private ArrayList<Integer> pointerTable;
    private InMemoryOutputStream pixelDataOutputStream;
    private int nextIndexInChecksumArea;
    private RawChecksumBuilder rawChecksumBuilder;
    private byte[] areaToChecksum;
    private SpriteData spriteData;

    public void writeInterweavedSpriteTableAndSpritesWithChecksumFixes(SpriteData spriteData, OutputStreamWithNot mainOutputStream) throws IOException {
        log.info("Attempting to weave sprites and add false data to fix checksums");
        RelativeByteOffsetOutputStream spritePackageOutputStream = new RelativeByteOffsetOutputStream(mainOutputStream);
        setupFromSpriteData(spriteData);
        buildInterweavedSpriteTableAndSpritesWithChecksumFixes();
        writeMetaToOutput(spriteData, spritePackageOutputStream);
        writePointerTableToOutput(spritePackageOutputStream);
        spritePackageOutputStream.writeBytes(pixelDataOutputStream.getBytes());
    }

    private void setupFromSpriteData(SpriteData spriteData) {
        this.spriteData = spriteData;
        potentialSpriteStartLocation = calculateAddressImmediatelyAfterTable();
        pointerTable = createDefaultPointerTable();
        pixelDataOutputStream = new InMemoryOutputStream(potentialSpriteStartLocation);
        nextIndexInChecksumArea = 0;
        rawChecksumBuilder = new RawChecksumBuilder();
        areaToChecksum = new byte[checksumSize];
    }

    private ArrayList<Integer> createDefaultPointerTable() {
        ArrayList<Integer> pointerTable = new ArrayList<>(spriteData.getSprites().size());
        int pixelStartLocation = calculateAddressImmediatelyAfterTable();
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            pointerTable.add(pixelStartLocation);
            pixelStartLocation += sprite.getByteCountAt16BitPerPixel();
        }
        pointerTable.add(pixelStartLocation); //location for sprite package termination bytes
        return pointerTable;
    }

    private int calculateAddressImmediatelyAfterTable() {
        //start of table + int pointer per sprite + int pointer to termination bytes
        return pixelPointerTableStart + 4* spriteData.getSprites().size() + 4;
    }

    private void buildInterweavedSpriteTableAndSpritesWithChecksumFixes() throws IOException {
        // this is kind of like the knapsack problem. I'm taking a greedy approach, but if we really needed to optimize,
        // it might be worth brushing up on the knapsack problem and look at rearranging sprites so smaller sprites
        // went where they fit between checksum areas even if it meant sprites were out of order.
        for(int index = 0 ; index <= spriteData.getSprites().size(); index++) {
            SpriteData.Sprite sprite;
            if(index < spriteData.getSprites().size()) {
                sprite = spriteData.getSprites().get(index);
            } else {
                sprite = TERMINATION_SPRITE;
            }
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
                        writeSpriteToInMemoryOutput(index, sprite);
                    } else {
                        // If bad, push checksum up to checksum start+2
                        writeSpriteToInMemoryOutputWithChecksumOffset(index, sprite, expectedChecksum, potentialSpriteStartLocation, nextIndexInChecksumArea);
                    }
                    nextIndexInChecksumArea = 0;
                } else if (index == spriteData.getSprites().size()) {
                    //this doesn't take up the rest of the area, but this is the last sprite
                    writeFinalSpriteToOutputWithChecksumOffset(index, sprite, potentialSpriteStartLocation, nextIndexInChecksumArea);
                } else {
                    // more in the area after this, so just write and keep going
                    int bytesInSprite = sprite.getByteCountAt16BitPerPixel();
                    copyBytes(sprite.getPixelData(), 0, areaToChecksum, nextIndexInChecksumArea, bytesInSprite);
                    nextIndexInChecksumArea += bytesInSprite;
                    writeSpriteToInMemoryOutput(index, sprite);
                }
            } else if(checksumStartsInSprite(sprite)) {
                int checksumStartLocation = SpriteChecksumBuilder.nextChecksumPortion(potentialSpriteStartLocation);
                int checksumEndLocation = SpriteChecksumBuilder.nextChecksumEnd(checksumStartLocation);
                if(checksumEndLocation <= potentialSpriteStartLocation + sprite.getByteCountAt16BitPerPixel()) {
                    //fully encompassed within this sprite
                    int expectedChecksum = getExpectedChecksumForNextBlock(); //this sprite starts out of the block and enters the next block
                    int checksumForNormalWrite = getChunkChecksumForNormalSpriteWrite(checksumStartLocation, sprite);
                    if(checksumForNormalWrite == expectedChecksum) {
                        //checksum matches so no need to move anything, write normally
                        writeSpriteToInMemoryOutput(index, sprite);
                    } else {
                        // If bad, push checksum up to checksum start+2
                        writeSpriteToInMemoryOutputWithChecksumOffset(index, sprite, expectedChecksum, checksumStartLocation, 0);
                    }
                } else if(index == spriteData.getSprites().size()) {
                    // Sprite only covers part of the checksum area, but this is the last sprite
                    writeFinalSpriteToOutputWithChecksumOffset(index, sprite, potentialSpriteStartLocation, 0);
                } else {
                    // Sprite only covers part of the checksum area
                    int startOfSpriteInChecksum = checksumStartLocation - potentialSpriteStartLocation;
                    //sprite 0 - startOfSpriteInChecksum is outside checksum, startOfSpriteInChecksum - end is inside checksum
                    int bytesInChecksumArea = sprite.getByteCountAt16BitPerPixel() - startOfSpriteInChecksum;
                    copyBytes(sprite.getPixelData(), startOfSpriteInChecksum, areaToChecksum, 0, bytesInChecksumArea);
                    nextIndexInChecksumArea = bytesInChecksumArea;
                    // We add the sprite here. If the checksum is no good we'll add an offset between this sprite and the next.
                    writeSpriteToInMemoryOutput(index, sprite);
                }
            } else {
                //we don't start in a checksum, and we don't enter a checksum, so just write normally
                writeSpriteToInMemoryOutput(index, sprite);
            }
        }
    }

    private void writeMetaToOutput(SpriteData spriteData, ByteOffsetOutputStream outputStream) throws IOException {
        outputStream.writeBytes(spriteData.getText().getBytes());
        outputStream.writeZerosUntilOffset(SpriteWriter.TERMINATION_BYTES_OF_POINTER_TABLE);
        int terminationBytesPointer = pointerTable.get(pointerTable.size()-1);
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(terminationBytesPointer));
        outputStream.writeZerosUntilOffset(SpriteWriter.TABLE_START);
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(1)); // 0x40
        //pointer to number of sprites
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(SpriteWriter.NUMBER_OF_SPRITES_LOCATION)); // 0x44
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(spriteData.getSprites().size())); // 0x48
    }

    private void writePointerTableToOutput(ByteOffsetOutputStream outputStream) throws IOException {
        for(Integer pointer : pointerTable) {
            outputStream.writeInt(pointer);
        }
    }

    private void writeSpriteToInMemoryOutput(int index, SpriteData.Sprite sprite) throws IOException {
        log.info("Sprite {} written normally.", index);
        pointerTable.set(index, potentialSpriteStartLocation);
        pixelDataOutputStream.writeBytes(sprite.getPixelData());
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
        return getExpectedChecksumForBlockAtLocation(potentialSpriteStartLocation);
    }

    private int getExpectedChecksumForBlockAtLocation(int location) {

        int checksumPortionIndex = SpriteChecksumBuilder.calculateWhichChunk(location);
        if(checksumPortionIndex < 0) {
            checksumPortionIndex = 0;
        }
        return spriteData.getSpriteChecksums().get(checksumPortionIndex);
    }

    private int getExpectedChecksumForNextBlock() {
        int nextChecksumStart = SpriteChecksumBuilder.nextChecksumPortion(potentialSpriteStartLocation);
        int checksumPortionIndex = SpriteChecksumBuilder.calculateWhichChunk(nextChecksumStart);
        return spriteData.getSpriteChecksums().get(checksumPortionIndex);
    }

    private void writeFinalSpriteToOutputWithChecksumOffset(int index, SpriteData.Sprite sprite, int dataStartLocation, int areaToChecksumOffset) throws IOException{
        // We can always put the checksum fix before the sprite for this case.
        copyBytes(sprite.getPixelData(), 0, areaToChecksum, areaToChecksumOffset, sprite.getPixelData().length);
        // fill the rest of the array with 0s since there is nothing after this.
        fillZeroes(areaToChecksum, areaToChecksumOffset + sprite.getByteCountAt16BitPerPixel(), areaToChecksum.length);
        rawChecksumBuilder.reset();
        rawChecksumBuilder.addBytes(areaToChecksum);
        int currentChecksum = rawChecksumBuilder.getChecksum();
        int expectedChecksum = getExpectedChecksumForBlockAtLocation(dataStartLocation + sprite.getByteCountAt16BitPerPixel());
        if(currentChecksum == expectedChecksum) {
            writeSpriteToInMemoryOutput(index, sprite);
        } else {
            writeSpriteToInMemoryOutputWithChecksumOffset(index, sprite, expectedChecksum, dataStartLocation, areaToChecksumOffset);
        }

    }

    private void fillZeroes(byte[] data, int start, int end) {
        for(int i = start; i < end; i++) {
            data[i] = 0;
        }
    }

    private void writeSpriteToInMemoryOutputWithChecksumOffset(int index, SpriteData.Sprite sprite, int expectedChecksum, int dataStartLocation, int areaToChecksumOffset) throws IOException {
        log.info("Sprite {} written with offset. DataStarting at: 0x{}", index, Integer.toHexString(dataStartLocation));
        areaToChecksum[areaToChecksumOffset] = 0;
        areaToChecksum[areaToChecksumOffset + 1] = 0;
        copyBytes(sprite.getPixelData(), 0, areaToChecksum, 2, areaToChecksum.length-(2 + areaToChecksumOffset));
        rawChecksumBuilder.reset();
        rawChecksumBuilder.addBytes(areaToChecksum);
        int checksumOffset = SpriteChecksumBuilder.calculateChecksumOffset(expectedChecksum, rawChecksumBuilder.getChecksum());
        pixelDataOutputStream.writeZerosUntilOffset(dataStartLocation); //should be no-op if the dataStartLocation isn't the start of the checksum area
        pixelDataOutputStream.write16BitInt(checksumOffset);
        //assumes that a single sprite will never cross multiple checksum areas.
        pixelDataOutputStream.writeBytes(sprite.getPixelData());
        pointerTable.set(index, dataStartLocation + 2);
        potentialSpriteStartLocation = dataStartLocation+2 + sprite.getByteCountAt16BitPerPixel();
    }

    private void copyBytes(byte[] src, int srcStart, byte[] dst, int endStart, int length) {
        log.debug("First from src: {} Last from src: {} First in dst: {} Last in dst: {}", srcStart, srcStart + (length-1), endStart, endStart + (length-1));
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
        private int location;
        private ByteArrayOutputStream byteArrayOutputStream;

        public byte[] getBytes() {
            return byteArrayOutputStream.toByteArray();
        }

        public InMemoryOutputStream(int startingLocation) {
            this.location = startingLocation;
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
            byteArrayOutputStream.write(bytes);
            location += bytes.length;
        }
    }
}
