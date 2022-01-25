package com.github.cfogrady.vb.dim.reader.writer;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;

import java.io.IOException;

public class SpriteDimentionsWriter {
    public static void writeSpriteDimensions(SpriteData spriteData, OutputStreamWithNot outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x60000);
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(sprite.getWidth()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(sprite.getHeight()));
        }
    }
}
