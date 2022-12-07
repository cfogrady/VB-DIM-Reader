package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.RawChecksumBuilder;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.SpriteChecksumBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
public class SpriteWriter {
    public static final int SPRITE_SECTION_START = 0x100_000;
    public static final int PIXEL_POINTER_TABLE_START = 0x100_04C;
    public static void writeSpriteData(SpriteData spriteData, boolean hasSpriteSigning, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(SPRITE_SECTION_START);
        if(hasSpriteSigning) {
            SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder();
            InMemoryOutputStream inMemoryOutputStream = new InMemoryOutputStream(spriteChecksumBuilder, SPRITE_SECTION_START);
            if(areSpriteChecksumsEqual(spriteChecksumBuilder.getChecksums(), spriteData.getSpriteChecksums())) {
                outputStreamWithNot.writeBytes(inMemoryOutputStream.getBytes());
            } else {
                writeSpriteDataToMatchChecksum(outputStreamWithNot, spriteData);
            }
        } else {
            writeUnmodified(spriteData, outputStreamWithNot);
        }

    }

    private static void writeSpriteDataToMatchChecksum(OutputStreamWithNot outputStreamWithNot, SpriteData spriteData) throws IOException {
        // this is kind of like the knapsack problem. I'm taking a greedy approach, but if we really needed to optimize,
        // it might be worth brushing up on the knapsack problem and look at rearranging sprites so smaller sprites
        // went where they fit between checksum areas even if it meant sprites were out of order.
        SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder();
        int potentialSpriteStartLocation = PIXEL_POINTER_TABLE_START + 4*spriteData.getSprites().size(); // start location + count int + pointer int per sprite
        ArrayList<Integer> pointerTable = createDefaultPointerTable(spriteData);
        InMemoryOutputStream inMemoryPixelDataOutputStream = new InMemoryOutputStream(spriteChecksumBuilder, potentialSpriteStartLocation);
        boolean currentlyInSignedArea = false;
        RawChecksumBuilder rawChecksumBuilder = new RawChecksumBuilder();
        byte[] areaToChecksum = new byte[0x1000];
        for(int index = 0 ; index < spriteData.getSprites().size(); index++) {
            SpriteData.Sprite sprite = spriteData.getSprites().get(index);
            if(currentlyInSignedArea) {
                //sharing a checksum area with a previous sprite
                //this may not be needed depending on how we continue / backtrack
            } else if(checkSumStartsInSprite(potentialSpriteStartLocation, sprite)) {
                int checksumStartLocation = SpriteChecksumBuilder.nextChecksumPortion(potentialSpriteStartLocation);
                int checksumEndLocation = SpriteChecksumBuilder.nextChecksumEnd(checksumStartLocation);
                if(checksumEndLocation <= potentialSpriteStartLocation + sprite.getByteCountAt16BitPerPixel()) {
                    //fully encompassed within this sprite
                    // test checksum portion against known checksum. If good, continue.
                    // If bad, push checksum up to checksum start+2, check if sprite still goes past end.
                    // If so, calculate checksum offset, and write it. If sprite went past end before, then moving
                    // the start of the sprite up will gaurantee that the end of the sprite is still past the end of the
                    // checksum.
                } else {
                    // copy portion of sprite that fits into checksum. Add next sprite. checksum matches, write and continue
                    // If not add space between this sprite and next sprite and calculate checksum offset
                }
            } else {
                //we don't start in a checksum, and we don't enter a checksum
                pointerTable.set(index, potentialSpriteStartLocation);
                inMemoryPixelDataOutputStream.writeBytes(sprite.getPixelData());
                potentialSpriteStartLocation += sprite.getByteCountAt16BitPerPixel();
            }
        }
    }

    private static boolean checkSumStartsInSprite(int spriteStartLocation, SpriteData.Sprite sprite) {
        int spriteEndLocation = spriteStartLocation + sprite.getByteCountAt16BitPerPixel();
        return SpriteChecksumBuilder.nextChecksumPortion(spriteStartLocation) < spriteEndLocation;

    }

    private static ArrayList<Integer> createDefaultPointerTable(SpriteData spriteData) {
        ArrayList<Integer> pointerTable = new ArrayList<>(spriteData.getSprites().size());
        int pixelStartLocation = PIXEL_POINTER_TABLE_START + 4*pointerTable.size();
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            pointerTable.add(pixelStartLocation);
            pixelStartLocation += sprite.getByteCountAt16BitPerPixel();
        }
        return pointerTable;
    }

    private static int calculateFinalOffset(int offsetUntilFirstSprite, List<SpriteData.Sprite> sprites) {
        int offset = offsetUntilFirstSprite;
        for(SpriteData.Sprite sprite : sprites) {
            offset = offset + (sprite.getWidth() * sprite.getHeight() * 2);
        }
        return offset;
    }

    private static void writeUnmodified(SpriteData spriteData, ByteOffsetOutputStream outputStream) throws IOException {
        outputStream.writeBytes(spriteData.getText().getBytes());
        outputStream.writeZerosUntilOffset(0x100018);
        //location of first offset sprite + int for each sprite + final int for the end location of the last sprite
        int offsetForFirstSprite = 0x4c + spriteData.getSprites().size() * 4 + 4;
        int finalOffset = calculateFinalOffset(offsetForFirstSprite, spriteData.getSprites());
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(finalOffset));
        outputStream.writeZerosUntilOffset(0x100040);
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(1)); // 0x40
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(72)); // 0x44
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(spriteData.getSprites().size())); // 0x48
        // Pointer Table
        int currentOffset = offsetForFirstSprite;
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(currentOffset));
            currentOffset = currentOffset + sprite.getWidth() * sprite.getHeight() * 2;
        }
        outputStream.writeBytes(ByteUtils.convert32BitIntToBytes(currentOffset));
        // Pixel Data
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            outputStream.writeBytes(sprite.getPixelData());
        }
        // Termination bytes
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(65282));
        outputStream.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
    }

    private static boolean areSpriteChecksumsEqual(List<Integer> checksum1, List<Integer> checksum2) {
        if(checksum1.size() != checksum2.size()) {
            return false;
        }
        Iterator<Integer> iter1 = checksum1.iterator();
        Iterator<Integer> iter2 = checksum2.iterator();
        while(iter1.hasNext()) {
            int val1 = iter1.next();
            int val2 = iter2.next();
            if(val1 != val2) {
                return false;
            }
        }
        return true;
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
