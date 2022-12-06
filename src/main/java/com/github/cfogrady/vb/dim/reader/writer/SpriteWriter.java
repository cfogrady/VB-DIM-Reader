package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.RawChecksumBuilder;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.SpriteChecksumBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class SpriteWriter {
    public static final int SPRITE_SECTION_START = 0x100_000;
    public static void writeSpriteData(SpriteData spriteData, boolean hasSpriteSigning, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(SPRITE_SECTION_START);
        if(hasSpriteSigning) {
            SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder();
            InMemoryOutputStream inMemoryOutputStream = new InMemoryOutputStream(spriteChecksumBuilder, SPRITE_SECTION_START);
            if(areSpriteChecksumsEqual(spriteChecksumBuilder.getChecksums(), spriteData.getSpriteChecksums())) {
                outputStreamWithNot.writeBytes(inMemoryOutputStream.getBytes());
            } else {
                //do the weave!!!!
            }
        } else {
            writeUnmodified(spriteData, outputStreamWithNot);
        }

    }

    private void writeSpriteDataToMatchChecksum(OutputStreamWithNot outputStreamWithNot, SpriteData spriteData) {
        SpriteChecksumBuilder spriteChecksumBuilder = new SpriteChecksumBuilder();
        InMemoryOutputStream inMemoryOutputStream = new InMemoryOutputStream(spriteChecksumBuilder, SPRITE_SECTION_START);
        int potentialSpriteStartLocation = 0x100_048 + 4 + 4*spriteData.getSprites().size(); // start location + count int + pointer int per sprite
        boolean currentlyInSignedAreay = SpriteChecksumBuilder.isPartOfChecksum(potentialSpriteStartLocation);
        RawChecksumBuilder rawChecksumBuilder = new RawChecksumBuilder();
        byte[] areaToChecksum = new byte[0x1000];
        int index = 0;
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            int spriteSizeInBytes = sprite.getHeight() * sprite.getWidth() * 2;
            if(SpriteChecksumBuilder.includesChecksumArea(potentialSpriteStartLocation, spriteSizeInBytes)) {

            }
        }

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
