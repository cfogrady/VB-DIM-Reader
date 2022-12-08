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
    public static SpriteChecksumHacker checksumHacker = new SpriteChecksumHacker(PIXEL_POINTER_TABLE_START, SpriteChecksumBuilder.CHUNK_CHECKSUM_PORTION);
    public static void writeSpriteData(SpriteData spriteData, boolean hasSpriteSigning, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(SPRITE_SECTION_START);
        if(hasSpriteSigning) {
            writeSpriteDataToMatchChecksum(outputStreamWithNot, spriteData);
        } else {
            writeUnmodified(spriteData, outputStreamWithNot);
        }

    }

    private static void writeSpriteDataToMatchChecksum(OutputStreamWithNot outputStreamWithNot, SpriteData spriteData) throws IOException {
        checksumHacker.writeInterweavedSpriteTableAndSpritesWithChecksumFixes(spriteData, outputStreamWithNot);
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
}
