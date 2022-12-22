package com.github.cfogrady.vb.dim.sprite;

import com.github.cfogrady.vb.dim.util.ByteOffsetOutputStream;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetOutputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class BemSpriteWriter {

    void writeSpriteDimensions(List<SpriteData.SpriteDimensions> spriteDimensions, ByteOffsetOutputStream generalOutputStream) {
        try {
            RelativeByteOffsetOutputStream relativeOutputStream = new RelativeByteOffsetOutputStream(generalOutputStream);
            for(SpriteData.SpriteDimensions dimension : spriteDimensions) {
                relativeOutputStream.write16BitInt(dimension.getWidth());
                relativeOutputStream.write16BitInt(dimension.getHeight());
            }
        } catch(IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
    public void writeSpriteDimensions(SpriteData spriteData, ByteOffsetOutputStream generalOutputStream) {
        List<SpriteData.SpriteDimensions> spriteDimensions = spriteData.getSpriteDimensions();
        writeSpriteDimensions(spriteDimensions, generalOutputStream);
    }

    public void writeSpritePackage(SpriteData spriteData, boolean hasSpriteSigning, ByteOffsetOutputStream generalOutputStream) {
        try {
            SpriteWriter.writeSpriteData(spriteData, hasSpriteSigning, generalOutputStream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
