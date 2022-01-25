package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SpriteWriter {
    public static void writeSpriteData(SpriteData spriteData, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x100000);
        outputStreamWithNot.writeBytes(spriteData.getText().getBytes());
        outputStreamWithNot.writeZerosUntilOffset(0x100018);
        //location of first offset sprite + int for each sprite + final int for the end location of the last sprite
        int offsetForFirstSprite = 0x4c + spriteData.getSprites().size() * 4 + 4;
        int finalOffset = calculateFinalOffset(offsetForFirstSprite, spriteData.getSprites());
        outputStreamWithNot.writeBytes(ByteUtils.convert32BitIntToBytes(finalOffset));
        outputStreamWithNot.writeZerosUntilOffset(0x100040);
        outputStreamWithNot.writeBytes(ByteUtils.convert32BitIntToBytes(1)); // 0x40
        outputStreamWithNot.writeBytes(ByteUtils.convert32BitIntToBytes(72)); // 0x44
        outputStreamWithNot.writeBytes(ByteUtils.convert32BitIntToBytes(spriteData.getSprites().size())); // 0x48
        int currentOffset = offsetForFirstSprite;
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert32BitIntToBytes(currentOffset));
            currentOffset = currentOffset + sprite.getWidth() * sprite.getHeight() * 2;
        }
        outputStreamWithNot.writeBytes(ByteUtils.convert32BitIntToBytes(currentOffset));
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            outputStreamWithNot.writeBytes(sprite.getPixelData());
        }
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(65282));
        outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(DimWriter.NONE_VALUE));
    }

    private static int calculateFinalOffset(int offsetUntilFirstSprite, List<SpriteData.Sprite> sprites) {
        int offset = offsetUntilFirstSprite;
        for(SpriteData.Sprite sprite : sprites) {
            offset = offset + (sprite.getWidth() * sprite.getHeight() * 2);
        }
        return offset;
    }
}
