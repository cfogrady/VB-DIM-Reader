package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.util.OutputStreamWithNot;

import java.io.IOException;

public class SpriteDimentionsWriter {
    public static void writeSpriteDimensions(SpriteData spriteData, ByteOffsetOutputStream outputStreamWithNot) throws IOException {
        outputStreamWithNot.writeZerosUntilOffset(0x60000);
        writeSpriteDimensionsAtCurrentLocation(spriteData, outputStreamWithNot);
    }

    static void writeSpriteDimensionsAtCurrentLocation(SpriteData spriteData, ByteOffsetOutputStream outputStreamWithNot) throws IOException {
        for(SpriteData.Sprite sprite : spriteData.getSprites()) {
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(sprite.getWidth()));
            outputStreamWithNot.writeBytes(ByteUtils.convert16BitIntToBytes(sprite.getHeight()));
        }
    }
}
