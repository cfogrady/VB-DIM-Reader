package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class SpriteWriter {
    public static final int SPRITE_SECTION_START = 0x100_000;
    public static final int TABLE_START = 0x40;
    public static final int PIXEL_POINTER_TABLE_START = 0x4C;
    public static final int NUMBER_OF_SPRITES_LOCATION = 0x48;
    public static final int TERMINATION_BYTES_OF_POINTER_TABLE = 0x18;
    public static final int TERMINATION_BYTES = 0xFFFFFF02;

    private final UnorderedSpriteChecksumHacker unorderedChecksumHacker;


    public void writeSpriteData(SpriteData spriteData, boolean hasSpriteSigning, ByteOffsetOutputStream outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(SPRITE_SECTION_START);
        if(hasSpriteSigning) {
            writeSpriteDataToMatchChecksum(outputStreamWithNot, spriteData);
        } else {
            writeUnmodified(spriteData, outputStreamWithNot);
        }
    }

    void writeSpriteDataToMatchChecksum(ByteOffsetOutputStream outputStreamWithNot, SpriteData spriteData) throws IOException {
        unorderedChecksumHacker.writeSpritesUnorderedToCorrectChecksums(spriteData, outputStreamWithNot);
    }

    private static int calculateTerminationBytesLocation(int firstSpritePointer, List<SpriteData.Sprite> sprites) {
        int offset = firstSpritePointer;
        for(SpriteData.Sprite sprite : sprites) {
            offset = offset + (sprite.getWidth() * sprite.getHeight() * 2);
        }
        return offset;
    }

    void writeUnmodified(SpriteData spriteData, ByteOffsetOutputStream outputStream) throws IOException {
        RelativeByteOffsetOutputStream relativeOutputStream = new RelativeByteOffsetOutputStream(outputStream);
        relativeOutputStream.writeBytes(spriteData.getText().getBytes());
        relativeOutputStream.writeZerosUntilOffset(TERMINATION_BYTES_OF_POINTER_TABLE);
        //location of first offset sprite = int pointer for each sprite + int pointer for the termination bytes
        int firstSpritePointer = PIXEL_POINTER_TABLE_START + spriteData.getSprites().size() * 4 + 4;
        int terminationBytesPointer = calculateTerminationBytesLocation(firstSpritePointer, spriteData.getSprites());
        relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(terminationBytesPointer));
        relativeOutputStream.writeZerosUntilOffset(TABLE_START);
        relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(1)); // 0x40
        //pointer to number of sprites
        relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(NUMBER_OF_SPRITES_LOCATION)); // 0x44
        relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(spriteData.getSprites().size())); // 0x48
        // Pointer Table
        int currentSpritePointer = firstSpritePointer;
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(currentSpritePointer));
            currentSpritePointer = currentSpritePointer + sprite.getWidth() * sprite.getHeight() * 2;
        }
        relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(currentSpritePointer));
        // Pixel Data
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            relativeOutputStream.writeBytes(sprite.getPixelData());
        }
        // Termination bytes
        relativeOutputStream.writeBytes(ByteUtils.convert32BitIntToBytes(TERMINATION_BYTES));
    }
}
